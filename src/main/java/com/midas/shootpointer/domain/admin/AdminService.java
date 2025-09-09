package com.midas.shootpointer.domain.admin;

import com.midas.shootpointer.domain.member.entity.Member;
import com.midas.shootpointer.domain.member.repository.MemberCommandRepository;
import com.midas.shootpointer.domain.member.repository.MemberQueryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;


@Service
@RequiredArgsConstructor
public class AdminService {
    
    private final MemberCommandRepository memberCommandRepository;
    private final MemberQueryRepository memberQueryRepository;
    
    // AccessToken으로 멤버 조회 후 없으면 생성
    public Member syncMember(String email, String nickname) {
        return memberQueryRepository.findByEmail(email)
            .orElseGet(() -> memberCommandRepository.save(
                Member.builder()
                    .email(email)
                    .username(nickname)
                    .build()
            ));
    }
}
