package com.midas.shootpointer.domain.memberbacknumber.repository;

import com.midas.shootpointer.domain.memberbacknumber.entity.MemberBackNumberEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MemberBackNumberRepository extends JpaRepository<MemberBackNumberEntity,Long> {
}
