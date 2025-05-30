package com.midas.shootpointer.adapter.in.web;

import com.midas.shootpointer.adapter.out.MemberRepository;
import com.midas.shootpointer.domain.member.MemberTest;
import lombok.AllArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class MemberTestRunner implements CommandLineRunner {

    private final MemberRepository memberRepository;

    @Override
    public void run(String... args) throws Exception {
        MemberTest member1 = new MemberTest("user_006");
        MemberTest saveMember1 = memberRepository.save(member1);
        System.out.println("저장된 회원 UUID : " + saveMember1.getId());
    }
}
