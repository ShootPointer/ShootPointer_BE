package com.midas.shootpointer.domain.like.helper;

import com.midas.shootpointer.domain.like.entity.LikeEntity;
import com.midas.shootpointer.domain.member.entity.Member;
import com.midas.shootpointer.domain.post.entity.PostEntity;
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
class LikeHelperImplTest {
    @InjectMocks
    private LikeHelperImpl likeHelper;

    @Mock
    private LikeValidation likeValidation;

    @Mock
    private LikeUtil likeUtil;

    @Test
    @DisplayName("올바른 유저의 좋아요 요청인지 확인합니다.-likeValidation.isValidCreateLike(memberId,postId) 실행 여부를 확인합니다.")
    void isValidCreateLike() {
        //given
        UUID memberId=UUID.randomUUID();
        Long postId=1L;

        //when
        likeHelper.isValidCreateLike(memberId,postId);

        //then
        verify(likeValidation, times(1)).isValidCreateLike(memberId,postId);
    }

    @Test
    @DisplayName("좋아요의 개수를 증가시킵니다.- likeUtil.increaseLikeCnt(post) 실행 여부를 확인합니다.")
    void increaseLikeCnt() {
        //given
        PostEntity post=mock(PostEntity.class);

        //when
        likeHelper.increaseLikeCnt(post);

        //then
        verify(likeUtil,times(1)).increaseLikeCnt(post);
    }

    @Test
    @DisplayName("좋아요의 개수를 감소시킵니다. - likeUtil.decreaseLikeCnt(post) 실행 여부를 확인합니다.")
    void decreaseLikeCnt() {
        //given
        PostEntity post=mock(PostEntity.class);

        //when
        likeHelper.decreaseLikeCnt(post);

        //then
        verify(likeUtil,times(1)).decreaseLikeCnt(post);
    }

    @Test
    @DisplayName("좋아요 객체를 생성합니다. - likeUtil.createLike(post,member) 실행 여부를 확인합니다.")
    void createLike() {
        //given
        PostEntity post=mock(PostEntity.class);
        Member member=mock(Member.class);

        //when
        likeHelper.createLike(post,member);

        //then
        verify(likeUtil,times(1)).createLike(post,member);
    }

    @Test
    @DisplayName("좋아요 객체를 삭제합니다. - likeUtil.deleteLike(like) 실행 여부를 확인합니다.")
    void deleteLike() {
        //given
        LikeEntity like=mock(LikeEntity.class);

        //when
        likeHelper.deleteLike(like);

        //then
        verify(likeUtil,times(1)).deleteLike(like);
    }

    @Test
    @DisplayName("게시물 Id와 유저 Id를 이용하여 좋아요 객체를 조회합니다. - likeUtil.findByPostIdAndMemberId(memberId,postId) 실행 여부를 확인합니다.")
    void findByPostIdAndMemberId() {
        //given
        UUID memberId=UUID.randomUUID();
        Long postId=10L;

        //when
        likeHelper.findByPostIdAndMemberId(memberId,postId);

        //then
        verify(likeUtil,times(1)).findByPostIdAndMemberId(memberId,postId);
    }
}