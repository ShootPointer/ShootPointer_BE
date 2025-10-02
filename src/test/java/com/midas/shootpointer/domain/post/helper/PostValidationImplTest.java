package com.midas.shootpointer.domain.post.helper;

import com.midas.shootpointer.domain.highlight.repository.HighlightQueryRepository;
import com.midas.shootpointer.domain.member.entity.Member;
import com.midas.shootpointer.domain.post.entity.PostEntity;
import com.midas.shootpointer.domain.post.helper.simple.PostValidationImpl;
import com.midas.shootpointer.global.common.ErrorCode;
import com.midas.shootpointer.global.exception.CustomException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowableOfType;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PostValidationImplTest {
    @InjectMocks
    private PostValidationImpl postValidation;

    @Mock
    private HighlightQueryRepository highlightQueryRepository;

    @Mock
    private PostEntity postEntity;

    @Mock
    private Member member1;

    @Mock
    private Member member2;

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
    @DisplayName("유저의 게시물이 아닌 경우 IS_NOT_MEMBERS_POST 예외가 발생합니다.")
    public void isMembersPost() {
        //given
        UUID memberId=UUID.randomUUID();
        UUID differentId=UUID.randomUUID();
       when(postEntity.getMember()).thenReturn(member1);
       when(member1.getMemberId()).thenReturn(memberId);

       when(member2.getMemberId()).thenReturn(differentId);

        //when
        CustomException customException=catchThrowableOfType(()->
                        postValidation.isMembersPost(postEntity,member2),
                CustomException.class
        );

        //then
        assertThat(customException).isNotNull();
        assertThat(customException.getErrorCode()).isEqualTo(ErrorCode.IS_NOT_MEMBERS_POST);
    }

    @Test
    @DisplayName("게시판 조회 시 유효하지 않은 (size가 음수이거나 500초과) 값 입력 시 IS_NOT_VALID_SIZE 예외가 발생합니다.")
    void isValidSize(){
        //given
        int minusSize=-12;
        int overSize=1000;

        //when
        CustomException exception1=catchThrowableOfType(()->
                        postValidation.isValidSize(minusSize),
                        CustomException.class
        );
        CustomException exception2=catchThrowableOfType(()->
                        postValidation.isValidSize(overSize),
                CustomException.class
        );

        //then
        assertThat(exception1).isNotNull();
        assertThat(exception2).isNotNull();

        assertThat(exception1.getErrorCode()).isEqualTo(ErrorCode.IS_NOT_VALID_SIZE);
        assertThat(exception2.getErrorCode()).isEqualTo(ErrorCode.IS_NOT_VALID_SIZE);
    }

    @DisplayName("입력값이 공백이면 false를 반환합니다.")
    @Test
    void isValidInput(){
        //given
        String input1="";
        String input2=" ";
        String input3=" input";

        //when
        boolean bool1=postValidation.isValidInput(input1);
        boolean bool2=postValidation.isValidInput(input2);
        boolean bool3=postValidation.isValidInput(input3);

        //then
        assertThat(bool1).isFalse();
        assertThat(bool2).isFalse();
        assertThat(bool3).isTrue();

    }

}