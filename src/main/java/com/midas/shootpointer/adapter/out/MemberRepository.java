package com.midas.shootpointer.adapter.out;

import com.midas.shootpointer.domain.member.MemberTest;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface MemberRepository extends JpaRepository<MemberTest, UUID> {
}
