package com.midas.shootpointer.domain.highlight.repository;

import com.midas.shootpointer.domain.member.entity.Member;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;
@SpringBootTest
class HighlightQueryRepositoryTest {
    @Autowired
    private HighlightQueryRepository highlightQueryRepository;

    @Test
    @DisplayName("멤버id와 하이라이트id가 일치하는 하이라이트 존재 시 true를 반환하는 쿼리입니다.")
    void isMemberHighlight_TRUE() {
        //given
        Member member
        //when

        //then
    }

    /**
     * Mock Member
     */
    private Member mockMember(){
        return Member.builder()
                .memberId()
    }
}