package com.midas.shootpointer.domain.post.helper;

import com.midas.shootpointer.domain.highlight.entity.HighlightEntity;
import com.midas.shootpointer.domain.member.entity.Member;
import com.midas.shootpointer.domain.post.entity.PostEntity;
import com.midas.shootpointer.domain.post.helper.simple.PostHelperImpl;
import com.midas.shootpointer.domain.post.helper.simple.PostUtil;
import com.midas.shootpointer.domain.post.helper.simple.PostValidation;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
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
    @DisplayName("게시물 id로 낙관적 락을 적용하여 게시물 entity를 조회합니다. - postUtil.findByPostByPostIdWithPessimisticLock(postId) 메서드가 실행되는지 확인합니다.")
    void findByPostByPostIdWithPessimisticLock(){
        //given
        Long postId=10L;
        when(postUtil.findByPostByPostIdWithPessimisticLock(postId)).thenReturn(postEntity);

        //when
        postHelper.findByPostByPostIdWithPessimisticLock(postId);

        //then
        verify(postUtil,times(1)).findByPostByPostIdWithPessimisticLock(postId);
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

    @Test
    @DisplayName("게시판을 검색 조회합니다 - postUtil.getPostEntitiesByPostTitleOrPostContent(String search) 메서드가 실행되는지 확인합니다.")
    void getPostEntitiesByPostTitleOrPostContent(){
        //given
        String search="제목";
        Long lastPostId=12312412424L;
        int size=10;
        when(postUtil.getPostEntitiesByPostTitleOrPostContent(search,lastPostId,size)).thenReturn(List.of(postEntity));

        //when
        postHelper.getPostEntitiesByPostTitleOrPostContent(search,lastPostId,size);

        //then
        verify(postUtil,times(1)).getPostEntitiesByPostTitleOrPostContent(search,lastPostId,size);
    }

    @Test
    @DisplayName("게시판 요청 size의 범위가 유효한 지 검사합니다. - postValidation.isValidSize(size) 메서드가 실행되는지 확인합니다.")
    void isValidSize(){
        //given
        int size=10;

        //when
        postHelper.isValidSize(size);

        //then
        verify(postValidation,times(1)).isValidSize(size);
    }

    @Test
    @DisplayName("게시판 요청 size의 범위가 유효한 지 검사합니다. - postValidation.isValidSize(size) 메서드가 실행되는지 확인합니다.")
    void isValidInput(){
        //given
        String input="input";

        //when
        postHelper.isValidInput(input);

        //then
        verify(postValidation,times(1)).isValidInput(input);
    }

    @Test
    @DisplayName("게시판 요청 size의 범위가 유효한 지 검사합니다. - postValidation.isValidSize(size) 메서드가 실행되는지 확인합니다.")
    void isValidAndGetPostOrderType(){
        //given
        String type="type";

        //when
        postHelper.isValidAndGetPostOrderType(type);

        //then
        verify(postUtil,times(1)).isValidAndGetPostOrderType(type);
    }
    
    @Test
    @DisplayName("memberId로 게시물 ID 목록을 조회합니다 - postUtil.findPostIdsByMemberId(memberId) 메서드가 실행되는지 확인합니다.")
    void findPostIdsByMemberId() {
        //given
        UUID memberId = UUID.randomUUID();
        List<Long> expectedPostIds = List.of(1L, 2L, 3L);
        when(postUtil.findPostIdsByMemberId(memberId)).thenReturn(expectedPostIds);
        
        //when
        List<Long> result = postHelper.findPostIdsByMemberId(memberId);
        
        //then
        assertThat(result).hasSize(3);
        assertThat(result).containsExactly(1L, 2L, 3L);
        verify(postUtil, times(1)).findPostIdsByMemberId(memberId);
    }
    
    @Test
    @DisplayName("게시물 ID 목록으로 게시물 엔티티들을 조회합니다 - postUtil.findPostsByPostIds(postIds) 메서드가 실행되는지 확인합니다.")
    void findPostsByPostIds() {
        //given
        List<Long> postIds = List.of(1L, 2L, 3L);
        List<PostEntity> expectedPosts = List.of(postEntity, newPostEntity, postEntity);
        when(postUtil.findPostsByPostIds(postIds)).thenReturn(expectedPosts);
        
        //when
        List<PostEntity> result = postHelper.findPostsByPostIds(postIds);
        
        //then
        assertThat(result).hasSize(3);
        verify(postUtil, times(1)).findPostsByPostIds(postIds);
    }

    @Test
    @DisplayName("memberId를 이용해 유저 자신이 좋아요를 누른 게시물을 생성일자 순으로 조회합니다. - postUtil.getMyLikedPost(memberId,lastPostId,size)")
    void getMyLikedPost(){
        //given
        UUID memberId=UUID.randomUUID();
        Long lastPostId=Long.MAX_VALUE;
        List<PostEntity> expectedPosts=List.of(postEntity,postEntity);
        when(postUtil.getMyLikedPost(memberId,lastPostId,10)).thenReturn(expectedPosts);

        //when
        List<PostEntity> result=postHelper.getMyLikedPost(memberId,lastPostId,10);

        //then
        assertThat(result).hasSize(2);
        verify(postUtil,times(1)).getMyLikedPost(memberId,lastPostId,10);
    }
}