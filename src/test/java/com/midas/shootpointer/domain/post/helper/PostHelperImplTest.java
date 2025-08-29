package com.midas.shootpointer.domain.post.helper;

import com.midas.shootpointer.domain.member.entity.Member;
import com.midas.shootpointer.domain.post.entity.PostEntity;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
@ExtendWith(MockitoExtension.class)
class PostHelperImplTest {
    @InjectMocks
    private PostHelperImpl postHelper;

    @Mock
    private PostValidation postValidation;

    @Mock
    private PostEntity postEntity;

    @Mock
    private Member member;

    @Test
    @DisplayName("유저의 하이라이트 영상인지 확인합니다-postValidation.isValidateHighlightId 메서드가 실행되는지 확인합니다.")
    void isValidateHighlightId() {
        //given
        UUID mockUUID=UUID.randomUUID();
        Member mockMember=mock(Member.class);

        //when
        postHelper.isValidateHighlightId(mockMember,mockUUID);

        //then
        verify(postValidation,times(1)).isValidateHighlightId(any(Member.class),any(UUID.class));
    }

    @Test
    @DisplayName("유저의 해시태그가 올바른지 확인합니다-postValidation.isValidPostHashTag 메서드가 실행되는지 확인합니다.")
    void isValidPostHashTag() {
        //given
        Object mockObject=new Object();

        //when
        postHelper.isValidPostHashTag(mockObject);

        //then
        verify(postValidation,times(1)).isValidPostHashTag(mockObject);
    }

    @Test
    @DisplayName("유저의 게시물인지 확인합니다-postValidation.isMembersPost 메서드가 실행되는지 확인합니다.")
    void isMembersPost(){
        //given
        doNothing().when(postValidation).isMembersPost(postEntity,member);

        //when
        postHelper.isMembersPost(postEntity,member);

        //then
        verify(postValidation,times(1)).isMembersPost(postEntity,member);
    }
}