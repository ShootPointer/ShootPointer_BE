package com.midas.shootpointer.domain.highlight.entity;

import com.midas.shootpointer.domain.member.entity.Member;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class HighlightEntityTest {

    @Test
    @DisplayName("하이라이트 객체의 2점 슛 총합계를 계산합니다.")
    void totalTwoPoint(){
        //given
        UUID memberId=UUID.randomUUID();

        Member member=Member.builder()
                .email("test@naver.com")
                .username("test")
                .memberId(memberId)
                .build();

        HighlightEntity highlight=HighlightEntity.builder()
                .member(member)
                .highlightKey(UUID.randomUUID())
                .highlightURL("url")
                .threePointCount(10)
                .twoPointCount(23)
                .build();

        //when
        int total=highlight.totalTwoPoint();

        //then
        assertThat(total).isEqualTo(23*2);
    }

    @Test
    @DisplayName("하이라이트 객체의 3점 슛 총합계를 계산합니다.")
    void totalThreePoint(){
        //given
        UUID memberId=UUID.randomUUID();

        Member member=Member.builder()
                .email("test@naver.com")
                .username("test")
                .memberId(memberId)
                .build();

        HighlightEntity highlight=HighlightEntity.builder()
                .member(member)
                .highlightKey(UUID.randomUUID())
                .highlightURL("url")
                .threePointCount(10)
                .twoPointCount(23)
                .build();

        //when
        int total=highlight.totalThreePoint();

        //then
        assertThat(total).isEqualTo(10*3);

    }
}