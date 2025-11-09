package com.midas.shootpointer.domain.memberbacknumber.repository;

import com.midas.shootpointer.domain.backnumber.entity.BackNumberEntity;
import com.midas.shootpointer.domain.member.entity.Member;
import com.midas.shootpointer.domain.memberbacknumber.entity.MemberBackNumberEntity;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface MemberBackNumberRepository extends JpaRepository<MemberBackNumberEntity,Long> {
    Optional<MemberBackNumberEntity> findByBackNumberAndMember( BackNumberEntity backNumber,Member member);
    
    @Query("SELECT mbn FROM MemberBackNumberEntity mbn WHERE mbn.member.memberId = :memberId")
    Optional<MemberBackNumberEntity> findByMemberId(@Param("memberId") UUID memberId);
}
