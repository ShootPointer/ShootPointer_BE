package com.midas.shootpointer.domain.member.business;

import com.midas.shootpointer.domain.member.dto.KakaoDTO;
import com.midas.shootpointer.domain.member.entity.Member;
import com.midas.shootpointer.domain.member.helper.MemberHelper;
import com.midas.shootpointer.domain.member.mapper.MemberMapper;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class MemberManager {
    private final MemberHelper memberHelper;
    private final MemberMapper memberMapper;
    
    public void validateKakaoCallback(HttpServletRequest request) {
        memberHelper.validateKakaoCallback(request);
    }
    
    public Member processKakaoLogin(HttpServletRequest request, KakaoDTO kakaoDTO) {
        memberHelper.validateKakaoCallback(request);
        memberHelper.validateKakaoDTO(kakaoDTO);
        
        return findOrCreateMember(kakaoDTO);
    }
    
    public UUID deleteMember(UUID memberId, Member currentMember) {
        memberHelper.isMemberOwner(currentMember, memberId);
        Member member = memberHelper.findMemberById(memberId);
        memberHelper.delete(member);
        
        return member.getMemberId();
    }
    
    public Member findMemberById(UUID memberId) {
        return memberHelper.findMemberById(memberId);
    }

    public UUID agree(Member member){
        member.agree();
        return member.getMemberId();
    }

    private Member findOrCreateMember(KakaoDTO kakaoDTO) {
        try {
            return memberHelper.findMemberByEmail(kakaoDTO.getEmail());
        } catch (Exception e) {
            // 회원이 없으면 신규 생성
            Member member = memberMapper.dtoToEntity(kakaoDTO);
            return memberHelper.save(member);
        }
    }
}
