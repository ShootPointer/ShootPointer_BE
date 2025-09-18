package com.midas.shootpointer.domain.memberbacknumber.helper;

import com.midas.shootpointer.domain.backnumber.entity.BackNumberEntity;
import com.midas.shootpointer.domain.member.entity.Member;
import com.midas.shootpointer.domain.memberbacknumber.entity.MemberBackNumberEntity;

public interface MemberBackNumberUtil {
    MemberBackNumberEntity findOrElseGetMemberBackNumber(Member member, BackNumberEntity backNumberEntity);
}
