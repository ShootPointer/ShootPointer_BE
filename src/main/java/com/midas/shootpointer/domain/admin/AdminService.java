package com.midas.shootpointer.domain.admin;

import com.midas.shootpointer.domain.member.entity.Member;
import com.midas.shootpointer.domain.member.repository.MemberRepository;
import com.midas.shootpointer.global.util.jwt.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;


@Service
@RequiredArgsConstructor
public class AdminService {

    private final MemberRepository memberRepository;

    public Member testToken(String token, JwtUtil jwtUtil) {
        String email = jwtUtil.getEmailFromToken(token);
        String nickname = jwtUtil.getNicknameFromToken(token);

        return memberRepository.findByEmail(email)
                .orElseGet(() -> {
                    Member member = Member.builder()
                            .email(email)
                            .username(nickname)
                            .build();
                    return memberRepository.save(member);
                });
    }
}
