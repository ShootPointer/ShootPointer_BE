package com.midas.shootpointer.domain.memberbacknumber.helper;

import com.midas.shootpointer.domain.backnumber.entity.BackNumberEntity;
import com.midas.shootpointer.domain.member.entity.Member;
import com.midas.shootpointer.domain.memberbacknumber.entity.MemberBackNumberEntity;

import java.util.UUID;

public interface MemberBackNumberUtil {
    MemberBackNumberEntity findOrElseGetMemberBackNumber(Member member, BackNumberEntity backNumberEntity);
    BackNumberEntity findByMemberId(UUID memberId);
}
