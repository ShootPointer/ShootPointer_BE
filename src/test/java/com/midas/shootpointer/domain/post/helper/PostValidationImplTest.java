package com.midas.shootpointer.domain.post.helper;

import com.midas.shootpointer.domain.highlight.entity.HighlightEntity;
import com.midas.shootpointer.domain.highlight.repository.HighlightQueryRepository;
import com.midas.shootpointer.domain.member.entity.Member;
import com.midas.shootpointer.domain.post.entity.HashTag;
import com.midas.shootpointer.domain.post.entity.PostEntity;
import com.midas.shootpointer.global.common.ErrorCode;
import com.midas.shootpointer.global.exception.CustomException;
import org.assertj.core.api.Assertions;
import org.junit.Assert;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PostValidationImplTest {
    @InjectMocks
    private PostValidationImpl postValidation;

    @Mock
    private HighlightQueryRepository highlightQueryRepository;

    @Test
    @DisplayName("멤버의 하이라이트가 아니면 IS_NOT_CORRECT_MEMBERS_HIGHLIGHT_ID 에외가 발생합니다.")
    void isValidateHighlightId() {
        //given
        UUID mockHighlightUUID = UUID.randomUUID();
        UUID mockMemberId = UUID.randomUUID();
        Member mockMember = mock(Member.class);

        //when
        when(mockMember.getMemberId()).thenReturn(mockMemberId);
        when(highlightQueryRepository.existsByHighlightIdAndMember(mockHighlightUUID,mockMemberId )).thenReturn(false);
        CustomException customException = catchThrowableOfType(() ->
                        postValidation.isValidateHighlightId(mockMember, mockHighlightUUID),
                        CustomException.class
        );

        //then
        assertThat(customException).isNotNull();
        assertThat(customException.getErrorCode()).isEqualTo(ErrorCode.IS_NOT_CORRECT_MEMBERS_HIGHLIGHT_ID);
    }

    @Test
    @DisplayName("유저가 입력한 ENUM 값이 HashTag class와 일치하지 않으면 IS_NOT_CORRECT_HASH_TAG 에외가 발생합니다.")
    void isValidPostHashTag() {
        //given
        Object mockObject=new Object();

        //when
        CustomException customException=catchThrowableOfType(()->
                postValidation.isValidPostHashTag(mockObject),
                CustomException.class
        );

        //then
        assertThat(customException).isNotNull();
        assertThat(customException.getErrorCode()).isEqualTo(ErrorCode.IS_NOT_CORRECT_HASH_TAG);
    }

    @Test
    @DisplayName("게시물이 삭제된 상태이면 DELETED_POST 예외가 발생합니다.")
    public void PostValidationImplTest() throws Exception{
        //given
        Member member=mockMember();
        HighlightEntity highlightEntity=mockHighlight();
        PostEntity postEntity=mockPost(member,highlightEntity);

        //when
        CustomException customException=catchThrowableOfType(()->
                postValidation.isDeleted(postEntity),
                CustomException.class
        );

        //then
        assertThat(customException).isNotNull();
        assertThat(customException.getErrorCode()).isEqualTo(ErrorCode.DELETED_POST);
    }

    /**
     * mockMember
     * @return Member
     */
    private Member mockMember() {
        return Member.builder()
                .memberId(UUID.randomUUID())
                .email("test@naver.com")
                .username("test")
                .build();
    }

    /**
     * @param member 유저 엔티티
     * @param highlight 하이라이트 엔티티
     * @return PostEntity
     */
    private PostEntity mockPost(Member member, HighlightEntity highlight){
        PostEntity postEntity= PostEntity.builder()
                .highlight(highlight)
                .member(member)
                .hashTag(HashTag.TWO_POINT)
                .title("title")
                .content("content")
                .build();
        postEntity.delete();
        return postEntity;
    }

    /**
     *
     * @return 하이라이트 엔티티
     */
    private HighlightEntity mockHighlight(){
        return HighlightEntity.builder()
                .highlightKey(UUID.randomUUID())
                .highlightURL("test")
                .build();
    }
}