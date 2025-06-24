package com.midas.shootpointer.domain.member.controller;

import com.midas.shootpointer.domain.member.dto.KakaoDTO;
import com.midas.shootpointer.domain.member.service.KakaoService;
import com.midas.shootpointer.domain.member.entity.MsgEntity;
import com.midas.shootpointer.global.annotation.CustomLog;
import com.midas.shootpointer.global.common.ErrorCode;
import com.midas.shootpointer.global.exception.CustomException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@RestController
@RequiredArgsConstructor
@RequestMapping("/kakao")
public class KakaoController {

    private final KakaoService kakaoService;
    private final Set<String> processingCodes = ConcurrentHashMap.newKeySet();

    @CustomLog("카카오 소셜 로그인 및 JWT 발급")
    @GetMapping("/callback")
    public ResponseEntity<MsgEntity> callback(HttpServletRequest request) {
        String code = request.getParameter("code");

        if (code == null || code.isBlank()) {
            throw new CustomException(ErrorCode.INVALID_KAKAO_AUTH_CODE);
        }
        
        if (!processingCodes.add(code)) {
            throw new CustomException(ErrorCode.DUPLICATE_REQUEST);
        }

        try {
            KakaoDTO kakaoInfo = kakaoService.getKakaoInfo(code);
            return ResponseEntity.ok().body(new MsgEntity("Success", kakaoInfo));
        } finally {
            processingCodes.remove(code);
        }
    }
}
