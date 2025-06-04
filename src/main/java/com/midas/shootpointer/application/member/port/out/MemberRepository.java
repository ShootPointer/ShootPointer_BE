package com.midas.shootpointer.application.member.port.out;

import com.midas.shootpointer.domain.member.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MemberRepository extends JpaRepository<Member, Long> {

    Member save(Member member);
}
