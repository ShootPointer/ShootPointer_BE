package com.midas.shootpointer.domain.post.business;

import com.midas.shootpointer.domain.highlight.entity.HighlightEntity;
import com.midas.shootpointer.domain.highlight.helper.HighlightHelper;
import com.midas.shootpointer.domain.member.entity.Member;
import com.midas.shootpointer.domain.post.dto.response.PostListResponse;
import com.midas.shootpointer.domain.post.dto.response.PostResponse;
import com.midas.shootpointer.domain.post.entity.HashTag;
import com.midas.shootpointer.domain.post.entity.PostEntity;
import com.midas.shootpointer.domain.post.helper.PostHelper;
import com.midas.shootpointer.domain.post.mapper.PostMapper;
import com.midas.shootpointer.domain.post.repository.PostCommandRepository;
import com.midas.shootpointer.domain.post.repository.PostQueryRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PostManagerTest {
    @InjectMocks
    private PostManager postManager;

    @Mock
    private PostHelper postHelper;

    @Mock
    private HighlightHelper highlightHelper;

    @Mock
    private PostCommandRepository postCommandRepository;

    @Mock
    private PostQueryRepository postQueryRepository;

    @Mock
    private PostMapper postMapper;

    @Test
    @DisplayName(
            "Highlight URL이 유저의 영상으로 일치 여부를 검증하고 " +
            "해시태그가 올바른 지 여부를 확인하고 게시물을 저장하고 " +
            "반환값으로 게시물의 id를 반환합니다."
    )
    void save() {
        //given
        Member mockMember=mockMember();
        UUID randomUUID=UUID.randomUUID();
        HighlightEntity mockHighlight=mockHighlight(randomUUID);
        PostEntity mockPostEntity=mockPostEntity("",mockMember);

        //저장된 게시물
        PostEntity savedPostEntity=createAndSavedPostEntity(mockHighlight,mockMember,111L,"");

        when(highlightHelper.findHighlightByHighlightId(randomUUID)).thenReturn(mockHighlight);
        doNothing().when(postHelper).isValidateHighlightId(mockMember,randomUUID);
        doNothing().when(postHelper).isValidPostHashTag(mockPostEntity.getHashTag());
        when(postHelper.save(any(PostEntity.class))).thenReturn(savedPostEntity);


        //when
        Long savedPostId=postManager.save(mockMember,mockPostEntity,randomUUID);

        //then
        assertThat(savedPostId).isEqualTo(111L);
        assertThat(mockPostEntity.getHighlight()).isEqualTo(mockHighlight);

        verify(highlightHelper,times(1)).findHighlightByHighlightId(randomUUID);
        verify(postHelper,times(1)).isValidateHighlightId(mockMember,randomUUID);
        verify(postHelper,times(1)).isValidPostHashTag(mockPostEntity.getHashTag());
    }


    @Test
    @DisplayName("postHelper의 다양한 유효성 검증을 진행하고 게시물을 수정하고 성공 시 postId를 반환합니다.")
    void update(){
        Member mockMember = mockMember();
        UUID highlightId = UUID.randomUUID();
        HighlightEntity mockHighlight = mockHighlight(highlightId);
        PostEntity newPost=spy(PostEntity.builder()
                .content("content2")
                .hashTag(HashTag.TWO_POINT)
                .highlight(mockHighlight)
                .member(mockMember)
                .title("title2")
                .build());
        Long postId = 111L;
        PostEntity existedPost=createAndSavedPostEntity(mockHighlight,mockMember,postId,"exist");

        when(postHelper.findPostByPostId(postId)).thenReturn(existedPost);

        doNothing().when(postHelper).isMembersPost(existedPost, mockMember);

        when(highlightHelper.findHighlightByHighlightId(highlightId)).thenReturn(mockHighlight);
        doNothing().when(postHelper).isValidateHighlightId(mockMember, highlightId);
        doNothing().when(postHelper).isValidPostHashTag(any(HashTag.class));

        when(postHelper.update(newPost,existedPost,mockHighlight))
                .thenReturn(newPost);

        //when
        Long updatedPostId = postManager.update(newPost, mockMember, postId);

        //then
        assertThat(updatedPostId).isEqualTo(newPost.getPostId());
        verify(postHelper, times(1)).findPostByPostId(postId);
        verify(postHelper, times(1)).isMembersPost(existedPost, mockMember);
        verify(postHelper, times(1)).isValidateHighlightId(mockMember, highlightId);
        verify(postHelper, times(1)).isValidPostHashTag(newPost.getHashTag());
        verify(postHelper,times(1)).update(newPost,existedPost,mockHighlight);
    }

    @Test
    @DisplayName("postHelper의 다양한 유효성 검증을 진행하고 게시물을 삭제하고 성공 시 postId를 반환합니다.")
    void delete(){
        Member mockMember = mockMember();
        PostEntity mockPostEntity = spy(mockPostEntity("",mockMember));
        Long postId = 111L;

        when(postHelper.findPostByPostId(postId)).thenReturn(mockPostEntity);
        doNothing().when(postHelper).isMembersPost(mockPostEntity, mockMember);

        //when
        Long deletedPostId = postManager.delete(postId, mockMember);

        //then
        assertThat(deletedPostId).isEqualTo(mockPostEntity.getPostId());
        verify(postHelper, times(1)).findPostByPostId(postId);
        verify(postHelper, times(1)).isMembersPost(mockPostEntity, mockMember);
        verify(mockPostEntity, times(1)).delete();

    }

    @Test
    @DisplayName("type 값이 올바른지 확인하고 type 값에 맞는 postHelper의 메소드들을 호출합니다._POPULAR")
    void multiRead_POPULAR(){
        //given
        String type="popular";
        Long lastPostId=12L;
        int size=10;
        List<PostEntity> postEntities=new ArrayList<>();
        List<PostResponse> responses=new ArrayList<>();
        PostListResponse postListResponse=PostListResponse.of(lastPostId,responses);


        //when
        when(postHelper.isValidAndGetPostOrderType(type)).thenReturn(PostOrderType.popular);
        when(postHelper.getPopularPostListBySliceAndNoOffset(lastPostId,size)).thenReturn(postEntities);
        when(postMapper.entityToDto(postEntities)).thenReturn(postListResponse);

        postManager.multiRead(lastPostId,type,size);

        //then
        verify(postHelper,times(1)).getPopularPostListBySliceAndNoOffset(lastPostId,size);
        verify(postMapper,times(1)).entityToDto(postEntities);
    }

    @Test
    @DisplayName("type 값이 올바른지 확인하고 type 값에 맞는 postHelper의 메소드들을 호출합니다._LATEST")
    void multiRead_LATEST(){
        //given
        String type="latest";
        Long lastPostId=12L;
        int size=10;
        List<PostEntity> postEntities=new ArrayList<>();
        List<PostResponse> responses=new ArrayList<>();
        PostListResponse postListResponse=PostListResponse.of(lastPostId,responses);


        //when
        when(postHelper.isValidAndGetPostOrderType(type)).thenReturn(PostOrderType.latest);
        when(postHelper.getLatestPostListBySliceAndNoOffset(lastPostId,size)).thenReturn(postEntities);
        when(postMapper.entityToDto(postEntities)).thenReturn(postListResponse);

        postManager.multiRead(lastPostId,type,size);

        //then
        verify(postHelper,times(1)).getLatestPostListBySliceAndNoOffset(lastPostId,size);
        verify(postMapper,times(1)).entityToDto(postEntities);
    }

    /**
     * mock 하이라이트 영상
     * @return HighlightEntity
     */
    private HighlightEntity mockHighlight(UUID highlightId) {
        return HighlightEntity.builder()
                .highlightId(highlightId)
                .highlightURL("test")
                .highlightKey(UUID.randomUUID())
                .build();
    }



    /**
     * mock 게시물 엔티티
     * @return PostEntity
     */
    private PostEntity mockPostEntity(String str,Member member){
        return PostEntity.builder()
                .title("title"+str)
                .content("content"+str)
                .member(member)
                .hashTag(HashTag.TREE_POINT)
                .build();
    }

    /**
     * mock Member
     */
    private Member mockMember() {
        return Member.builder()
                .memberId(UUID.randomUUID())
                .email("test@naver.com")
                .username("test")
                .build();
    }

    private PostEntity createAndSavedPostEntity(HighlightEntity highlight,Member member,Long postId,String str){
        return PostEntity.builder()
                .member(member)
                .postId(postId)
                .highlight(highlight)
                .hashTag(HashTag.TREE_POINT)
                .content("content"+str)
                .title("title"+str)
                .build();
    }
}