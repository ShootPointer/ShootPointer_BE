package com.midas.shootpointer.domain.member.service;

import com.midas.shootpointer.domain.member.dto.KakaoDTO;
import com.midas.shootpointer.domain.member.repository.MemberRepository;
import com.midas.shootpointer.domain.member.entity.Member;
import com.midas.shootpointer.global.util.jwt.JwtUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import net.minidev.json.JSONObject;
import net.minidev.json.parser.JSONParser;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
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

    @Value("${spring.security.oauth2.client.registration.kakao.client-id}")
    private String KAKAO_CLIENT_ID;

    @Value("${spring.security.oauth2.client.registration.kakao.client-secret}")
    private String KAKAO_CLIENT_SECRET;

    @Value("${spring.security.oauth2.client.registration.kakao.redirect-uri}")
    private String KAKAO_REDIRECT_URI;

    private final static String KAKAO_AUTH_URI = "https://kauth.kakao.com"; // 카카오 계정 인증을 위한 URI
    private final static String KAKAO_API_URI = "https://kapi.kakao.com"; // 카카오 API에 접근할 때 사용되는 URI

    public String getKakaoLogin() {
        return KAKAO_AUTH_URI + "/oauth/authorize"
                + "?client_id=" + KAKAO_CLIENT_ID
                + "&redirect_uri=" + KAKAO_REDIRECT_URI
                + "&response_type=code";
    }
    // https://kauth.kakao.com/oauth/authorize?client_id=KAKAO_CLIENT_ID&redirect_uri=KAKAO_REDIRECT_URI&response_type=code

    public KakaoDTO getKakaoInfo(String code) throws Exception {
        if (code == null || code.isBlank()) {
            throw new IllegalArgumentException("인증 코드가 유효하지 않습니다.");
        }

        // 카카오에 액세스 토큰 요청
        String accessToken = getKakaoAccessToken(code);

        // 토큰으로 사용자 정보 요청
        KakaoDTO kakaoDTO = getUserInfoWithToken(accessToken);

        // 회원 중복 확인해야 함
        Optional<Member> existingMember = memberRepository.findByEmail(kakaoDTO.getEmail());

        if (existingMember.isEmpty()) { // 신규 회원이라면?
            // 신규 회원은 DB에 저장
            Member member = Member.builder()
                    .email(kakaoDTO.getEmail())
                    .username(kakaoDTO.getNickname())
                    .build();
            memberRepository.save(member);
        }

        // JWT 발급하고
        String jwt = jwtUtil.createToken(kakaoDTO.getEmail(), kakaoDTO.getNickname());

        // DTO에 JWT 포함하여 반환하기
        kakaoDTO.setJwt(jwt);
        return kakaoDTO;
    }

    // 카카오 AccessToken 요청
    private String getKakaoAccessToken(String code) throws Exception {
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", "application/x-www-form-urlencoded");

        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("grant_type", "authorization_code");
        params.add("client_id", KAKAO_CLIENT_ID);
        params.add("client_secret", KAKAO_CLIENT_SECRET);
        params.add("code", code);
        params.add("redirect_uri", KAKAO_REDIRECT_URI);

        RestTemplate restTemplate = new RestTemplate();
        HttpEntity<MultiValueMap<String, String>> httpEntity = new HttpEntity<>(params, headers);

        ResponseEntity<String> response = restTemplate.exchange(
                KAKAO_AUTH_URI + "/oauth/token",
                HttpMethod.POST,
                httpEntity,
                String.class
        );

        if (!response.getStatusCode().is2xxSuccessful()) {
            throw new Exception("카카오 토큰 요청 실패");
        }

        JSONParser jsonParser = new JSONParser();
        JSONObject jsonObj = (JSONObject) jsonParser.parse(response.getBody());
        return (String) jsonObj.get("access_token");
    }

    // AccessToken으로 회원 정보 획득
    private KakaoDTO getUserInfoWithToken(String accessToken) throws Exception {
        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "Bearer " + accessToken);

        RestTemplate rt = new RestTemplate();
        HttpEntity<?> httpEntity = new HttpEntity<>(headers);

        ResponseEntity<String> response = rt.exchange(
                KAKAO_API_URI + "/v2/user/me",
                HttpMethod.POST,
                httpEntity,
                String.class
        );

        if (!response.getStatusCode().is2xxSuccessful()) {
            throw new Exception("카카오 사용자 정보 요청 실패");
        }

        JSONParser jsonParser = new JSONParser();
        JSONObject jsonObj = (JSONObject) jsonParser.parse(response.getBody());
        JSONObject account = (JSONObject) jsonObj.get("kakao_account");
        JSONObject profile = (JSONObject) account.get("profile");

        // 회원 정보 추출
        long id = (long) jsonObj.get("id");
        String email = String.valueOf(account.get("email"));
        String nickname = String.valueOf(profile.get("nickname"));

        return KakaoDTO.builder()
                .id(id)
                .email(email)
                .nickname(nickname)
                .build();
    }


}
