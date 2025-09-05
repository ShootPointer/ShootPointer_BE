package com.midas.shootpointer.domain.member.repository;

import com.midas.shootpointer.domain.member.entity.Member;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MemberCommandRepository extends JpaRepository<Member, UUID> {

}
