package com.midas.shootpointer.domain.member.controller;

import com.midas.shootpointer.domain.member.dto.KakaoDTO;
import com.midas.shootpointer.domain.member.service.KakaoService;
import com.midas.shootpointer.domain.member.entity.MsgEntity;
import com.midas.shootpointer.global.annotation.CustomLog;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/kakao")
public class KakaoController {

    private final KakaoService kakaoService;

    @CustomLog("카카오 소셜 로그인 및 JWT 발급")
    @GetMapping("/callback")
    public ResponseEntity<MsgEntity> callback(HttpServletRequest request) throws Exception {
        KakaoDTO kakaoInfo = kakaoService.getKakaoInfo(request.getParameter("code"));

        return ResponseEntity.ok()
                .body(new MsgEntity("Success", kakaoInfo));
        // HTTP 응답을 생성, OK 응답을 반환하며, body로 MsgEntity 객체 담고 있음
    }
}
