package com.midas.shootpointer.domain.post.business.query;

import com.midas.shootpointer.domain.post.business.PostManager;
import com.midas.shootpointer.domain.post.dto.response.PostListResponse;
import com.midas.shootpointer.domain.post.dto.response.PostResponse;
import com.midas.shootpointer.domain.post.entity.HashTag;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PostQueryServiceImplTest {
    @InjectMocks
    private PostQueryServiceImpl postQueryService;

    @Mock
    private PostManager postManager;

    @Mock
    private PostListResponse postListResponse;
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

    private PostResponse makePostResponse(LocalDateTime time,Long postId){
        return PostResponse.builder()
                .content("content")
                .likeCnt(10L)
                .createdAt(time)
                .modifiedAt(time)
                .highlightUrl("test")
                .postId(postId)
                .title("title")
                .hashTag(HashTag.TWO_POINT)
                .build();

    }
}