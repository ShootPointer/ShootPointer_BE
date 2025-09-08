package com.midas.shootpointer.domain.member.business;

import com.midas.shootpointer.domain.member.business.command.TokenService;
import com.midas.shootpointer.domain.member.dto.KakaoDTO;
import com.midas.shootpointer.domain.member.entity.Member;
import com.midas.shootpointer.domain.member.business.command.KakaoService;
import com.midas.shootpointer.domain.member.business.command.MemberService;
import com.midas.shootpointer.domain.member.helper.ValidationHelper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class MemberManagerImpl implements MemberManager {
    
    private final KakaoService kakaoService;
    private final MemberService memberService;
    private final TokenService tokenService;
    private final ValidationHelper validationHelper;
    
    @Override
    public KakaoDTO processKakaoLogin(String code) {
        // 1. 코드 검증
        validationHelper.validateKakaoCode(code);
        
        // 2. 카카오에서 사용자 정보 획득
        KakaoDTO kakaoInfo = kakaoService.getKakaoUserInfo(code);
        
        // 3. 회원 처리 (신규 가입 또는 기존 회원 조회)
        Member member = memberService.findOrCreateMember(kakaoInfo);
        
        // 4. JWT 토큰 생성 및 설정
        tokenService.generateTokens(member, kakaoInfo);
        return kakaoInfo;
        
    }
}
