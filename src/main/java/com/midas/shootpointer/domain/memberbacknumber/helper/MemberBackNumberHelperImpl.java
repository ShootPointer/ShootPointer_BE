package com.midas.shootpointer.domain.memberbacknumber.helper;

import com.midas.shootpointer.domain.backnumber.entity.BackNumberEntity;
import com.midas.shootpointer.domain.member.entity.Member;
import com.midas.shootpointer.domain.memberbacknumber.entity.MemberBackNumberEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class MemberBackNumberHelperImpl implements MemberBackNumberHelper{
    private final MemberBackNumberUtil memberBackNumberUtil;
    @Override
    public MemberBackNumberEntity findOrElseGetMemberBackNumber(Member member, BackNumberEntity backNumberEntity) {
        return memberBackNumberUtil.findOrElseGetMemberBackNumber(member,backNumberEntity);
    }

    @Override
    public BackNumberEntity findByMemberId(UUID memberId) {
        return memberBackNumberUtil.findByMemberId(memberId);
    }
}
