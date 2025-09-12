package com.midas.shootpointer.domain.post.helper;

import com.midas.shootpointer.domain.highlight.entity.HighlightEntity;
import com.midas.shootpointer.domain.member.entity.Member;
import com.midas.shootpointer.domain.post.entity.PostEntity;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.UUID;

import static org.mockito.Mockito.*;
@ExtendWith(MockitoExtension.class)
class PostHelperImplTest {
    @InjectMocks
    private PostHelperImpl postHelper;

    @Mock
    private PostValidation postValidation;

    @Mock
    private PostUtil postUtil;

    @Mock
    private PostEntity postEntity;

    @Mock
    private PostEntity newPostEntity;

    @Mock
    private HighlightEntity highlightEntity;

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

    @Test
    @DisplayName("게시물을 저장합니다. - postUtil.save(postEntity) 메서드가 실행되는지 확인합니다.")
    void save(){
        //given
       when(postUtil.save(postEntity)).thenReturn(postEntity);

        //when
        postHelper.save(postEntity);

        //then
        verify(postUtil,times(1)).save(postEntity);
    }

    @Test
    @DisplayName("게시물id로 게시물 entity를 조회합니다. - postUtil.findPostByPostId(postId) 메서드가 실행되는지 확인합니다.")
    void find(){
        //given
        Long postId=111L;
        when(postUtil.findPostByPostId(postId)).thenReturn(postEntity);

        //when
        postHelper.findPostByPostId(postId);

        //then
        verify(postUtil,times(1)).findPostByPostId(postId);
    }

    @Test
    @DisplayName("게시물을 수정합니다. - postUtil.update(postRequest,post,highlight) 메서드가 실행되는지 확인합니다.")
    void update(){
        //given
        when(postUtil.update(newPostEntity,postEntity,highlightEntity)).thenReturn(postEntity);

        //when
        postHelper.update(newPostEntity,postEntity,highlightEntity);

        //then
        verify(postUtil,times(1)).update(newPostEntity,postEntity,highlightEntity);
    }

    @Test
    @DisplayName("게시판을 최신순으로 조회합니다. - postUtil.getLatestPostListBySliceAndNoOffset(Long postId, int size) 메서드가 실행되는지 확인합니다.")
    void getLatestPostListBySliceAndNoOffset(){
        //given
        Long postId=124134325L;
        int size=100;
        when(postUtil.getLatestPostListBySliceAndNoOffset(postId,size)).thenReturn(List.of(postEntity));

        //when
        postHelper.getLatestPostListBySliceAndNoOffset(postId,size);

        //then
        verify(postUtil,times(1)).getLatestPostListBySliceAndNoOffset(postId,size);
    }

    @Test
    @DisplayName("게시판을 좋아요순으로 조회합니다. - postUtil.getPopularPostListBySliceAndNoOffset(Long postId, int size) 메서드가 실행되는지 확인합니다.")
    void getPopularPostListBySliceAndNoOffset(){
        //given
        Long postId=124134325L;
        int size=100;
        when(postUtil.getPopularPostListBySliceAndNoOffset(postId,size)).thenReturn(List.of(postEntity));

        //when
        postHelper.getPopularPostListBySliceAndNoOffset(postId,size);

        //then
        verify(postUtil,times(1)).getPopularPostListBySliceAndNoOffset(postId,size);
    }
}