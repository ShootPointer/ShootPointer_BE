package com.midas.shootpointer.application.member.service;

import com.midas.shootpointer.application.member.port.out.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class MemberService {

    private final MemberRepository memberRepository;
}
