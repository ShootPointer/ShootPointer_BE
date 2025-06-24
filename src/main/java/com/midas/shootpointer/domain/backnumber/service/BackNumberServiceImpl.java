package com.midas.shootpointer.domain.backnumber.service;

import com.midas.shootpointer.domain.backnumber.dto.BackNumberRequest;
import com.midas.shootpointer.domain.backnumber.dto.BackNumberResponse;
import com.midas.shootpointer.domain.backnumber.entity.BackNumber;
import com.midas.shootpointer.domain.backnumber.entity.BackNumberEntity;
import com.midas.shootpointer.domain.backnumber.mapper.BackNumberMapper;
import com.midas.shootpointer.domain.backnumber.repository.BackNumberRepository;
import com.midas.shootpointer.domain.member.entity.Member;
import com.midas.shootpointer.domain.member.repository.MemberRepository;
import com.midas.shootpointer.domain.memberbacknumber.entity.MemberBackNumberEntity;
import com.midas.shootpointer.domain.memberbacknumber.repository.MemberBackNumberRepository;
import com.midas.shootpointer.global.annotation.CustomLog;
import com.midas.shootpointer.global.common.ErrorCode;
import com.midas.shootpointer.global.exception.CustomException;
import com.midas.shootpointer.global.util.jwt.JwtUtil;
import com.midas.shootpointer.infrastructure.openCV.service.OpenCVClient;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class BackNumberServiceImpl implements BackNumberService{
    private final BackNumberRepository backNumberRepository;
    private final MemberRepository memberRepository;
    private final MemberBackNumberRepository memberBackNumberRepository;
    private final OpenCVClient openCVClient;
    private final BackNumberMapper mapper;
    private final JwtUtil jwtUtil;

    /*==========================
    *
    *BackNumberServiceImpl
    *
    * @parm 등 번호 response dto
    * @return 등 번호 request dto
    * @author kimdoyeon
    * @version 1.0.0
    * @date 6/16/25
    *
    ==========================**/
    @Override
    @Transactional
    @CustomLog
    public BackNumberResponse create(String token, BackNumberRequest request, MultipartFile image) {
        UUID memberId = jwtUtil.getMemberId(token);
        //TODO: 임의로 멤버 id값 지정 및 예외처리 수정

        Member member = memberRepository.findByMemberId(memberId)
                .orElseThrow(() -> new CustomException(ErrorCode.MEMBER_NOT_FOUND));

        //1. 요청 등 번호가 있는지 확인
        BackNumber requestBackNumber=BackNumber.of(request.getBackNumber());
        BackNumberEntity backNumber=backNumberRepository.findByBackNumber(requestBackNumber)
                .orElseGet(()->{
                    BackNumberEntity newEntity = mapper.dtoToEntity(request);
                    return backNumberRepository.save(newEntity);
                });

        //2. 중간 테이블 저장
        memberBackNumberRepository.findByBackNumberAndMember(backNumber,member)
                .orElseGet(()->{
                    MemberBackNumberEntity newMemberBackNumber=MemberBackNumberEntity.of(
                            member,
                            backNumber
                    );
                    return memberBackNumberRepository.save(newMemberBackNumber);
                });


        //3. OpenCV 사진 전송
        try {
            openCVClient.sendBackNumberInformation(memberId, requestBackNumber.getNumber(), image,token);
        }catch (Exception e){
            throw new CustomException(ErrorCode.FAILED_SEND_IMAGE_TO_OPENCV);
        }

        return mapper.entityToDto(backNumber);
    }

}
