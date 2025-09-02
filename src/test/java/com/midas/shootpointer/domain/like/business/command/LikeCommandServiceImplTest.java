package com.midas.shootpointer.domain.like.business.command;

import com.midas.shootpointer.domain.like.business.LikeManager;
import com.midas.shootpointer.domain.member.entity.Member;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class LikeCommandServiceImplTest {
    @InjectMocks
    private LikeCommandServiceImpl likeCommandService;

    @Mock
    private LikeManager likeManager;

    @Test
    @DisplayName("좋아요를 증가시킵니다. - likeManager.increase(postId, member) 실행 여부를 확인합니다.")
    void create() {
        //given
        Member member= mock(Member.class);
        Long postId=100L;

        //when
        likeCommandService.create(postId,member);

        //then
        verify(likeManager,times(1)).increase(postId,member);
    }

    @Test
    @DisplayName("좋아요를 감소시킵니다. - likeManager.decrease(postId, member) 실행 여부를 확인합니다.")
    void delete() {
        //given
        Member member= mock(Member.class);
        Long postId=100L;

        //when
        likeCommandService.delete(postId,member);

        //then
        verify(likeManager,times(1)).decrease(postId,member);
    }
}