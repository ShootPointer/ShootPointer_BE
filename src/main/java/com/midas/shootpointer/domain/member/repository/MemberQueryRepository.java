package com.midas.shootpointer.domain.member.repository;

import com.midas.shootpointer.domain.member.entity.Member;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface MemberQueryRepository extends JpaRepository<Member, UUID> {
	Optional<Member> findByEmail(String email);
	Optional<Member> findByMemberId(UUID memberId);
	
	@Query("SELECT m FROM Member m WHERE m.email = :email")
	Optional<Member> findMemberByEmail(String email);
}
