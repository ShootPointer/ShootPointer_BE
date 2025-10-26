package com.midas.shootpointer.domain.highlight.entity;

import com.midas.shootpointer.domain.member.entity.Member;
import com.midas.shootpointer.global.common.ErrorCode;
import com.midas.shootpointer.global.exception.CustomException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;

class HighlightEntityTest {
    @Test
    @DisplayName("유저의 하이라이트 영상이 아닌 경우 IS_NOT_CORRECT_MEMBERS_HIGHLIGHT_ID 예외를 반환합니다.")
    void select_IS_NOT_USERS_HIGHLIGHT(){
        //given
        UUID memberId1=UUID.randomUUID();
        UUID memberId2=UUID.randomUUID();

        Member member1=Member.builder()
                .email("test@naver.com")
                .username("test")
                .memberId(memberId1)
                .build();
        Member member2=Member.builder()
                .email("test2@naver.com")
                .username("test2")
                .memberId(memberId2)
                .build();

        HighlightEntity highlight=HighlightEntity.builder()
                .member(member1)
                .highlightKey(UUID.randomUUID())
                .highlightURL("url")
                .build();

        //when & then
        assertThatThrownBy(() -> highlight.select(member2))
                .isInstanceOf(CustomException.class)
                .hasMessageContaining(ErrorCode.IS_NOT_CORRECT_MEMBERS_HIGHLIGHT_ID.getMessage());
    }

    @Test
    @DisplayName("이미 선택된 하이라이트인 경우 EXISTED_SELECTED 예외를 발생시킵니다.")
    void select_EXIST_SELECTED(){
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
                .isSelected(true)
                .build();

        //when & then
        assertThatThrownBy(() -> highlight.select(member))
                .isInstanceOf(CustomException.class)
                .hasMessageContaining(ErrorCode.EXISTED_SELECTED.getMessage());

    }

    @Test
    @DisplayName("select 메서드 실행 시 isSelected가 True로 변환됩니다.")
    void select(){
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
                .build();

        //when
        highlight.select(member);

        //then
        assertThat(highlight.getIsSelected()).isTrue();
    }

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