package com.midas.shootpointer.domain.post.dto.response;

import com.midas.shootpointer.domain.post.entity.HashTag;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class PostListResponseTest {
    @Test
    void of(){
        //given
        Long lastPostId=123L;
        List<PostResponse> postResponseList= List.of(
                makePostResponse(1L),
                makePostResponse(2L),
                makePostResponse(3L)
        );

        //when
        PostListResponse result=PostListResponse.of(lastPostId,postResponseList);

        //then
        assertThat(result.getLastPostId()).isEqualTo(123L);
        assertThat(result.getPostList()).hasSize(3);
        assertThat(result.getPostList().get(0).getPostId()).isEqualTo(1L);
        assertThat(result.getPostList().get(1).getPostId()).isEqualTo(2L);
        assertThat(result.getPostList().get(2).getPostId()).isEqualTo(3L);
    }

    @Test
    void withSort(){
        //given
        Long lastPostId=123L;
        PostSort sort=new PostSort(12f,12L,lastPostId);

        List<PostResponse> postResponseList= List.of(
                makePostResponse(1L),
                makePostResponse(2L),
                makePostResponse(3L)
        );

        //when
        PostListResponse result=PostListResponse.withSort(lastPostId,postResponseList,sort);

        //then
        assertThat(result.getPostList()).hasSize(3);
        assertThat(result.getLastPostId()).isEqualTo(lastPostId);
        assertThat(result.getPostList().get(0).getPostId()).isEqualTo(1L);
        assertThat(result.getPostList().get(1).getPostId()).isEqualTo(2L);
        assertThat(result.getPostList().get(2).getPostId()).isEqualTo(3L);

        assertThat(result.getSort()._score()).isEqualTo(sort._score());
        assertThat(result.getSort().lastPostId()).isEqualTo(sort.lastPostId());
        assertThat(result.getSort().likeCnt()).isEqualTo(sort.likeCnt());
    }


    private PostResponse makePostResponse(Long postId){
        return PostResponse.builder()
                .postId(postId)
                .modifiedAt(LocalDateTime.now())
                .likeCnt(23L)
                .highlightUrl("url")
                .memberName("name")
                .createdAt(LocalDateTime.now())
                .content("content")
                .hashTag(HashTag.THREE_POINT.getName())
                .title("title")
                .build();
    }
}