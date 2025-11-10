package com.midas.shootpointer.domain.memberbacknumber.helper;

import com.midas.shootpointer.domain.backnumber.entity.BackNumberEntity;
import com.midas.shootpointer.domain.member.entity.Member;
import com.midas.shootpointer.domain.memberbacknumber.entity.MemberBackNumberEntity;
import com.midas.shootpointer.domain.memberbacknumber.repository.MemberBackNumberRepository;
import com.midas.shootpointer.global.common.ErrorCode;
import com.midas.shootpointer.global.exception.CustomException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class MemberBackNumberUtilImpl implements MemberBackNumberUtil{
    private final MemberBackNumberRepository memberBackNumberRepository;

    @Override
    public MemberBackNumberEntity findOrElseGetMemberBackNumber(Member member, BackNumberEntity backNumberEntity) {
        return memberBackNumberRepository.findByBackNumberAndMember(backNumberEntity,member)
                .orElseGet(()->{
                    MemberBackNumberEntity newMemberBackNumber=MemberBackNumberEntity.of(
                            member,
                            backNumberEntity
                    );
                    return memberBackNumberRepository.save(newMemberBackNumber);
                });
    }

    @Override
    public BackNumberEntity findByMemberId(UUID memberId) {
        return memberBackNumberRepository.findByMember_MemberId(memberId)
                .orElseThrow(()->new CustomException(ErrorCode.BACK_NUMBER_NOT_FOUND));
    }
}
