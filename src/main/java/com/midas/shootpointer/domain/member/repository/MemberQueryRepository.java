package com.midas.shootpointer.domain.member.repository;

import com.midas.shootpointer.domain.member.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface MemberQueryRepository extends JpaRepository<Member, UUID> {
	Optional<Member> findByEmail(String email);
	Optional<Member> findByMemberId(UUID memberId);

	@Query("SELECT m FROM Member m WHERE m.email = :encryptedEmail")
	Optional<Member> findByEncryptedEmail(@Param("encryptedEmail") String encryptedEmail);
}
