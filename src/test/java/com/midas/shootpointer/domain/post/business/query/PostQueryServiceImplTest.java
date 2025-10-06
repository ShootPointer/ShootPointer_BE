package com.midas.shootpointer.domain.post.business.query;

import com.midas.shootpointer.domain.post.business.PostManager;
import com.midas.shootpointer.domain.post.dto.response.PostListResponse;
import com.midas.shootpointer.domain.post.dto.response.PostResponse;
import com.midas.shootpointer.domain.post.dto.response.PostSort;
import com.midas.shootpointer.domain.post.dto.response.SearchAutoCompleteResponse;
import com.midas.shootpointer.domain.post.entity.HashTag;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PostQueryServiceImplTest {
    @InjectMocks
    private PostQueryServiceImpl postQueryService;

    @Mock
    private PostManager postManager;

    @Mock
    private PostListResponse postListResponse;

    @Mock
    private List<SearchAutoCompleteResponse> searchAutoCompleteResponses;
    @Test
    @DisplayName("게시물 조회 시 postManager - singleRead(postId)의 호출을 확인합니다.")
    void singleRead(){
        //given
        Long postId=123124L;
        LocalDateTime time=LocalDateTime.now();
        PostResponse postResponse=makePostResponse(time,postId);

        //when
        when(postManager.singleRead(postId)).thenReturn(postResponse);
        postQueryService.singleRead(postId);

        //then
        verify(postManager,times(1)).singleRead(postId);
    }

    @Test
    @DisplayName("게시물 조회 시 postManager - multiRead(postId,size,type)의 호출을 확인합니다.")
    void multiRead(){
        //given
        Long postId=123124L;
        int size=10;
        String type="latest";

        //when
        when(postManager.multiRead(postId,type,size)).thenReturn(postListResponse);
        postQueryService.multiRead(postId,size,type);

        //then
        verify(postManager,times(1)).multiRead(postId,type,size);
    }

    @Test
    @DisplayName("게시물 조회 시 postManager - getPostEntitiesByPostTitleOrPostContent(search,postId,size)의 호출을 확인합니다.")
    void search(){
        //given
        Long postId=214124L;
        int size=10;
        String search="search";

        //when
        when(postManager.getPostEntitiesByPostTitleOrPostContent(search,postId,size)).thenReturn(postListResponse);
        postQueryService.search(search,postId,size);

        //then
        verify(postManager,times(1)).getPostEntitiesByPostTitleOrPostContent(search,postId,size);
    }

    @Test
    @DisplayName("게시물 검색 시 postManager - getPostByPostTitleOrPostContentByElasticSearch(search,size,sort)의 호출을 확인합니다.")
    void searchByElastic(){
        //given
        String search="search";
        int size=10;
        PostSort sort=new PostSort(10f,100L,123L);

        //when
        when(postManager.getPostByPostTitleOrPostContentByElasticSearch(search,size,sort)).thenReturn(postListResponse);
        postQueryService.searchByElastic(search,size,sort);

        //then
        verify(postManager,times(1)).getPostByPostTitleOrPostContentByElasticSearch(search,size,sort);
    }

    @Test
    @DisplayName("추천 검색의 리스트를 조회 시 postManager - searchAutoCompleteResponse(keyword)의 호출을 확인합니다.")
    void searchAutoCompleteResponse(){
        //given
        String keyword="keyword";

        //when
        when(postManager.suggest(keyword)).thenReturn(searchAutoCompleteResponses);
        postQueryService.suggest(keyword);

        //then
        verify(postManager,times(1)).suggest(keyword);
    }
    private PostResponse makePostResponse(LocalDateTime time,Long postId){
        return PostResponse.builder()
                .content("content")
                .likeCnt(10L)
                .createdAt(time)
                .modifiedAt(time)
                .highlightUrl("test")
                .postId(postId)
                .title("title")
                .hashTag(HashTag.TWO_POINT.getName())
                .build();

    }
}