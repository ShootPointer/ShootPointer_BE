package com.midas.shootpointer.domain.member.helper;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.midas.shootpointer.domain.member.dto.KakaoDTO;
import com.midas.shootpointer.global.common.ErrorCode;
import com.midas.shootpointer.global.exception.CustomException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

@Component
public class KakaoApiHelperImpl implements KakaoApiHelper {
	
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
	
	@Override
	public String requestAccessToken(String code) {
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
			
			ObjectMapper mapper = new ObjectMapper();
			JsonNode jsonNode = mapper.readTree(response.getBody());
			
			if (jsonNode.has("error")) {
				throw new CustomException(ErrorCode.KAKAO_TOKEN_REQUEST_FAIL);
			}
			
			return jsonNode.get("access_token").asText();
			
		} catch (Exception e) {
			throw new CustomException(ErrorCode.KAKAO_TOKEN_REQUEST_FAIL);
		}
	}
	
	@Override
	public KakaoDTO requestUserInfo(String accessToken) {
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
			
			ObjectMapper mapper = new ObjectMapper();
			JsonNode root = mapper.readTree(response.getBody());
			
			JsonNode kakaoAccount = root.path("kakao_account");
			JsonNode profile = kakaoAccount.path("profile");
			
			String email = kakaoAccount.path("email").asText(null);
			String nickname = profile.path("nickname").asText(null);
			
			if (email == null || email.isBlank() || nickname == null || nickname.isBlank()) {
				throw new CustomException(ErrorCode.KAKAO_USERINFO_FAIL);
			}
			
			return KakaoDTO.builder()
				.email(email)
				.nickname(nickname)
				.build();
			
		} catch (Exception e) {
			throw new CustomException(ErrorCode.KAKAO_USERINFO_FAIL);
		}
	}
}
