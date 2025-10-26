package com.midas.shootpointer.domain.member.entity;

import com.midas.shootpointer.global.common.ErrorCode;
import com.midas.shootpointer.global.exception.CustomException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class MemberTest {

    @Test
    @DisplayName("회원의 하이라이트 집계 동의를 true로 전환합니다.")
    void agree() {
        //given
        Member member=Member.builder()
                .username("test")
                .email("test@naver.com")
                .build();

        //when
        member.agree();

        //then
        assertThat(member.getIsAggregationAgreed()).isTrue();
    }

    @Test
    @DisplayName("회원의 하이라이트 집계 동의가 true인 상태에서 agree요청 시 IS_AGGREGATION_TRUE 예외를 반환합니다.")
    void agree_ERROR(){
        //given
        Member member=Member.builder()
                .username("test")
                .email("test@naver.com")
                .isAggregationAgreed(true)
                .build();

        //then
        assertThatThrownBy(()->member.agree())
                .isInstanceOf(CustomException.class)
                .hasMessage(ErrorCode.IS_AGGREGATION_TRUE.getMessage());

    }
}