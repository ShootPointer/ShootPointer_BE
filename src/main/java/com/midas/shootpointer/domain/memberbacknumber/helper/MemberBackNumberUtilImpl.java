package com.midas.shootpointer.domain.memberbacknumber.helper;

import com.midas.shootpointer.domain.backnumber.entity.BackNumberEntity;
import com.midas.shootpointer.domain.member.entity.Member;
import com.midas.shootpointer.domain.memberbacknumber.entity.MemberBackNumberEntity;
import com.midas.shootpointer.domain.memberbacknumber.repository.MemberBackNumberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

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
}
