package com.midas.shootpointer.domain.member.repository;

import com.midas.shootpointer.domain.member.entity.Member;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class MemberRepositoryTest {

    @Autowired
    private MemberRepository memberRepository;

    @Test
    @DisplayName("멤버 저장, 자동 PK 증가 확인")
    void 멤버저장_자동PK증가() {
        // given
        Member member = Member.builder()
                .username("홍길동")
                .email("test1234@naver.com")
                .build();

        // when
        Member savedMember = memberRepository.save(member);

        // then
        assertThat(savedMember.getMemberId()).isNotNull();
        System.out.println("생성된 memberId : " + savedMember.getMemberId());

        // when & then: findByEmail 정상동작 확인
        Optional<Member> found = memberRepository.findByEmail("test1234@naver.com");
        assertThat(found).isPresent();
        assertThat(found.get().getUsername()).isEqualTo("홍길동");

    }

}