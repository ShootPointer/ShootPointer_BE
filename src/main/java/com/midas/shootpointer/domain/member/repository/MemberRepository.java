package com.midas.shootpointer.domain.member.repository;

import com.midas.shootpointer.domain.member.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface MemberRepository extends JpaRepository<Member, UUID> {

    Optional<Member> findByEmail(String email);
    Optional<Member> findByMemberId(UUID memberId);
}
