package com.midas.shootpointer.domain.post.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.midas.shootpointer.domain.post.business.query.PostQueryService;
import com.midas.shootpointer.domain.post.dto.response.PostListResponse;
import com.midas.shootpointer.domain.post.dto.response.PostResponse;
import com.midas.shootpointer.domain.post.entity.HashTag;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class PostQueryControllerTest {
    private MockMvc mockMvc;

    private ObjectMapper objectMapper;

    @InjectMocks
    private PostQueryController postQueryController;

    @Mock
    private PostQueryService postQueryService;

    private final String baseUrl="/api/post";

    @BeforeEach
    void setUp(){
        mockMvc= MockMvcBuilders.standaloneSetup(postQueryController)
                .build();
        objectMapper=new ObjectMapper();
    }

    @Test
    @DisplayName("게시물 단건 조회 GET 요청 성공 시 postResponse를 반환합니다._SUCCESS")
    void singleRead() throws Exception {
        //given
        Long postId=123124L;
        PostResponse response=makePostResponse(LocalDateTime.now(),postId,10L,"","");

        //when
        when(postQueryService.singleRead(anyLong()))
                .thenReturn(response);

        //then
        mockMvc.perform(get(baseUrl+"/"+postId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("OK"))
                .andExpect(jsonPath("$.success").value(true))

                .andExpect(jsonPath("$.data.postId").value(response.getPostId()))
                .andExpect(jsonPath("$.data.title").value(response.getTitle()))
                .andExpect(jsonPath("$.data.content").value(response.getContent()))
                .andExpect(jsonPath("$.data.highlightUrl").value(response.getHighlightUrl()))
                .andExpect(jsonPath("$.data.likeCnt").value(response.getLikeCnt()))
                .andExpect(jsonPath("$.data.createdAt").value(getCreatedDateFormat(response)))
                .andExpect(jsonPath("$.data.modifiedAt").value(getModifiedDateFormat(response)))
                .andExpect(jsonPath("$.data.hashTag").value(response.getHashTag().toString()))
                .andDo(print());

        verify(postQueryService,times(1)).singleRead(postId);

    }

    @Test
    @DisplayName("게시물 다건 조회 GET 요청 성공 시 PostListResponse를 반환합니다-인기순._SUCCESS")
    void multiRead_POPULAR() throws Exception {
        //given
        String type="popular";
        Long postId=1000L;
        int size=2;

        List<PostResponse> postResponses=new ArrayList<>();
        postResponses.add(makePostResponse(LocalDateTime.now(), 1L,50L,"",""));
        postResponses.add(makePostResponse(LocalDateTime.now(), 2L,30L,"",""));

        PostListResponse expectedResponse=PostListResponse.of(postId,postResponses);

        //when
        when(postQueryService.multiRead(postId,size,type)).thenReturn(expectedResponse);

        //then
        mockMvc.perform(get(baseUrl)
                        .param("postId",postId.toString())
                        .param("size", String.valueOf(size))
                        .param("type",type))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("OK"))
                .andExpect(jsonPath("$.success").value(true))

                .andExpect(jsonPath("$.data.postList[0].postId").value(1L))
                .andExpect(jsonPath("$.data.postList[0].likeCnt").value(50L))
                .andExpect(jsonPath("$.data.postList[1].postId").value(2L))
                .andExpect(jsonPath("$.data.postList[1].likeCnt").value(30L))
                .andDo(print());

        verify(postQueryService,times(1)).multiRead(postId,size,type);
    }

    @Test
    @DisplayName("게시물 다건 조회 GET 요청 성공 시 PostListResponse를 반환합니다-최신순._SUCCESS")
    void multiRead_LATEST() throws Exception {
        //given
        String type="latest";
        Long postId=1000L;
        int size=2;

        List<PostResponse> postResponses=new ArrayList<>();
        postResponses.add(makePostResponse(LocalDateTime.now(), 1L,50L,"",""));
        postResponses.add(makePostResponse(LocalDateTime.now().minusDays(2L), 2L,30L,"",""));

        PostListResponse expectedResponse=PostListResponse.of(postId,postResponses);

        //when
        when(postQueryService.multiRead(postId,size,type)).thenReturn(expectedResponse);

        //then
        mockMvc.perform(get(baseUrl)
                        .param("postId",postId.toString())
                        .param("size", String.valueOf(size))
                        .param("type",type))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("OK"))
                .andExpect(jsonPath("$.success").value(true))

                .andExpect(jsonPath("$.data.postList[0].postId").value(1L))
                .andExpect(jsonPath("$.data.postList[0].likeCnt").value(50L))
                .andExpect(jsonPath("$.data.postList[1].postId").value(2L))
                .andExpect(jsonPath("$.data.postList[1].likeCnt").value(30L))
                .andDo(print());

        verify(postQueryService,times(1)).multiRead(postId,size,type);
    }

    @Test
    @DisplayName("게시물 검색 조회 GET 요청 성공 시 PostListResponse를 반환합니다._SUCCESS")
    void search() throws Exception {
        //given
        String search="test";
        Long postId=1000L;
        int size=10;

        List<PostResponse> postResponses=new ArrayList<>();
        postResponses.add(makePostResponse(LocalDateTime.now(), 1L,50L,"title1","content1"));
        postResponses.add(makePostResponse(LocalDateTime.now().minusDays(2L), 2L,30L,"title2","content2"));

        PostListResponse expectedResponse=PostListResponse.of(2L,postResponses);

        //when
        when(postQueryService.search(search,postId,size)).thenReturn(expectedResponse);

        //then
        mockMvc.perform(get(baseUrl+"/list")
                        .param("postId",postId.toString())
                        .param("size", String.valueOf(size))
                        .param("search",search))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("OK"))
                .andExpect(jsonPath("$.success").value(true))

                .andExpect(jsonPath("$.data.postList[0].postId").value(1L))
                .andExpect(jsonPath("$.data.postList[0].likeCnt").value(50L))
                .andExpect(jsonPath("$.data.postList[0].title").value("title1"))
                .andExpect(jsonPath("$.data.postList[0].content").value("content1"))

                .andExpect(jsonPath("$.data.postList[1].postId").value(2L))
                .andExpect(jsonPath("$.data.postList[1].likeCnt").value(30L))
                .andExpect(jsonPath("$.data.postList[1].title").value("title2"))
                .andExpect(jsonPath("$.data.postList[1].content").value("content2"))

                .andExpect(jsonPath("$.data.lastPostId").value(2L))
                .andDo(print());

        verify(postQueryService,times(1)).search(search,postId,size);

    }

    @NotNull
    private static String getCreatedDateFormat(PostResponse response) {
        return response.getCreatedAt().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
    }

    @NotNull
    private static String getModifiedDateFormat(PostResponse response) {
        return response.getModifiedAt().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
    }

    private PostResponse makePostResponse(LocalDateTime time, Long postId,Long likeCnt,String title,String content){
        return PostResponse.builder()
                .content(content)
                .likeCnt(likeCnt)
                .createdAt(time)
                .modifiedAt(time)
                .highlightUrl("test")
                .postId(postId)
                .title(title)
                .hashTag(HashTag.TWO_POINT)
                .build();

    }


}