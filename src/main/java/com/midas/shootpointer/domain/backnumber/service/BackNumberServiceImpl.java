package com.midas.shootpointer.domain.backnumber.service;

import com.midas.shootpointer.domain.backnumber.dto.BackNumberRequest;
import com.midas.shootpointer.domain.backnumber.dto.BackNumberResponse;
import com.midas.shootpointer.domain.backnumber.entity.BackNumber;
import com.midas.shootpointer.domain.backnumber.entity.BackNumberEntity;
import com.midas.shootpointer.domain.backnumber.mapper.BackNumberMapper;
import com.midas.shootpointer.domain.backnumber.repository.BackNumberRepository;
import com.midas.shootpointer.domain.member.entity.Member;
import com.midas.shootpointer.domain.member.repository.MemberRepository;
import com.midas.shootpointer.global.annotation.CustomLog;
import com.midas.shootpointer.global.common.ErrorCode;
import com.midas.shootpointer.global.exception.CustomException;
import com.midas.shootpointer.global.util.jwt.JwtUtil;
import com.midas.shootpointer.infrastructure.openCV.OpenCVClient;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class BackNumberServiceImpl implements BackNumberService{
    private final BackNumberRepository backNumberRepository;
    private final MemberRepository memberRepository;
    private final OpenCVClient openCVClient;
    private final BackNumberMapper mapper;
    private final JwtUtil jwtUtil;
    @Override
    @Transactional
    @CustomLog
    public BackNumberResponse create(BackNumberRequest request) {
        UUID userID=jwtUtil.getUserId();
        //TODO: 임의로 멤버 id값 지정 및 예외처리 수정
        Long memberId=1L;
        Member member=memberRepository.findByMemberId(memberId)
                .orElseThrow();

        //1. 유저의 등번호 저장
        BackNumberEntity backNumber=mapper.dtoToEntity(request);

        return null;
    }
}
