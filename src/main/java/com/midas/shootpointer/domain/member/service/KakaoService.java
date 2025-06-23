package com.midas.shootpointer.domain.member.service;

import com.midas.shootpointer.domain.member.dto.KakaoDTO;
import com.midas.shootpointer.domain.member.repository.MemberRepository;
import com.midas.shootpointer.domain.member.entity.Member;
import com.midas.shootpointer.global.annotation.CustomLog;
import com.midas.shootpointer.global.common.ErrorCode;
import com.midas.shootpointer.global.exception.CustomException;
import com.midas.shootpointer.global.util.jwt.JwtUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import net.minidev.json.JSONObject;
import net.minidev.json.parser.JSONParser;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class KakaoService {

    private final MemberRepository memberRepository;
    private final JwtUtil jwtUtil;
    private final RefreshTokenService refreshTokenService;

    @Value("${spring.security.oauth2.client.registration.kakao.client-id}")
    private String KAKAO_CLIENT_ID;

    @Value("${spring.security.oauth2.client.registration.kakao.client-secret}")
    private String KAKAO_CLIENT_SECRET;

    @Value("${spring.security.oauth2.client.registration.kakao.redirect-uri}")
    private String KAKAO_REDIRECT_URI;

    @Value("${kakao.auth.uri}")
    private String KAKAO_AUTH_URI;

    @Value("${kakao.api.uri}")
    private String KAKAO_API_URI;

    @CustomLog("== 카카오 로그인 oauth/authorize URL 반환 ==")
    private String getKakaoLogin() {
        return KAKAO_AUTH_URI + "/oauth/authorize"
                + "?client_id=" + KAKAO_CLIENT_ID
                + "&redirect_uri=" + KAKAO_REDIRECT_URI
                + "&response_type=code";
    }
    // https://kauth.kakao.com/oauth/authorize?client_id=KAKAO_CLIENT_ID&redirect_uri=KAKAO_REDIRECT_URI&response_type=code

    /**
     * code로 카카오 로그인 후 사용자 정보 받아오기
     * 신규 가입자면 eamil, nickname 암호화해서 저장
     * 기존에 있었다면 JWT만 발급해서 return
     */
    @Transactional
    @CustomLog("== 카카오 로그인 Process Start ==")
    public KakaoDTO getKakaoInfo(String code) {
        if (code == null || code.isBlank()) {
            throw new CustomException(ErrorCode.INVALID_KAKAO_AUTH_CODE);
        }

        // 카카오에 액세스 토큰 요청
        String accessToken = getKakaoAccessToken(code);

        // 토큰으로 사용자 정보 요청
        KakaoDTO kakaoDTO = getUserInfoWithToken(accessToken);

        // 회원 중복 확인해야 함
        Member member = memberRepository.findByEmail(kakaoDTO.getEmail())
                .orElseGet(() -> {
                    Member newMember = Member.builder()
                            .email(kakaoDTO.getEmail())
                            .username(kakaoDTO.getNickname())
                            .build();
                    return memberRepository.save(newMember);
                });

        // UUID(memberId) 기반 JWT AccessToken / RefreshToken 생성하는 로직
        String redisAccessToken = jwtUtil.createToken(member.getMemberId(), member.getEmail(), member.getUsername());
        String redisRefreshToken = jwtUtil.createRefreshToken(member.getEmail());

        refreshTokenService.save(member.getEmail(), redisRefreshToken);

        kakaoDTO.setAccessToken(redisAccessToken);
        kakaoDTO.setRefreshToken(redisRefreshToken);

        return kakaoDTO;
    }

    // 카카오 AccessToken 요청
    @CustomLog("== 카카오 Access Token 발급 ==")
    private String getKakaoAccessToken(String code){
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

            MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
            params.add("grant_type", "authorization_code");
            params.add("client_id", KAKAO_CLIENT_ID);
            params.add("client_secret", KAKAO_CLIENT_SECRET);
            params.add("code", code);
            params.add("redirect_uri", KAKAO_REDIRECT_URI);

            HttpEntity<MultiValueMap<String, String>> httpEntity = new HttpEntity<>(params, headers);

            RestTemplate restTemplate = new RestTemplate();
            ResponseEntity<String> response = restTemplate.exchange(
                    KAKAO_AUTH_URI + "/oauth/token",
                    HttpMethod.POST,
                    httpEntity,
                    String.class
            );

            if (!response.getStatusCode().is2xxSuccessful()) {
                throw new CustomException(ErrorCode.KAKAO_TOKEN_REQUEST_FAIL);
            }

            JSONParser jsonParser = new JSONParser();
            JSONObject jsonObj = (JSONObject) jsonParser.parse(response.getBody());
            String accessToken = (String) jsonObj.get("access_token");

            if (accessToken == null || accessToken.isBlank()) {
                throw new CustomException(ErrorCode.KAKAO_TOKEN_RESPONSE_INVALID);
            }
            return accessToken;
        } catch (CustomException e) {
            throw e;
        } catch (Exception e) {
            throw new CustomException(ErrorCode.KAKAO_TOKEN_REQUEST_FAIL);
        }
    }

    // Access Token으로 회원 정보 획득
    @CustomLog("== 카카오에 사용자 정보 요청하기 ==")
    private KakaoDTO getUserInfoWithToken(String accessToken) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.add("Authorization", "Bearer " + accessToken);

            RestTemplate rt = new RestTemplate();
            HttpEntity<?> httpEntity = new HttpEntity<>(headers);

            ResponseEntity<String> response = rt.exchange(
                    KAKAO_API_URI + "/v2/user/me",
                    HttpMethod.GET,
                    httpEntity,
                    String.class
            );

            if (!response.getStatusCode().is2xxSuccessful()) {
                throw new CustomException(ErrorCode.KAKAO_USERINFO_FAIL);
            }

            JSONParser jsonParser = new JSONParser();
            JSONObject jsonObj = (JSONObject) jsonParser.parse(response.getBody());
            JSONObject account = (JSONObject) jsonObj.get("kakao_account");
            JSONObject profile = (JSONObject) account.get("profile");

            String email = String.valueOf(account.get("email"));
            String nickname = String.valueOf(profile.get("nickname"));

            if (email == null || email.isBlank() || nickname == null || nickname.isBlank()) {
                throw new CustomException(ErrorCode.KAKAO_USERINFO_FAIL);
            }

            return KakaoDTO.builder()
                    .email(email)
                    .nickname(nickname)
                    .build();

        } catch (CustomException e) {
            throw new CustomException(ErrorCode.KAKAO_USERINFO_FAIL);
        } catch (Exception e) {
            throw new CustomException(ErrorCode.JSON_OBJECT_PARSE_FAIL);
        }
    }
}
