package com.midas.shootpointer.domain.post.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.midas.shootpointer.domain.post.business.query.PostQueryService;
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
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;

import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.time.format.DateTimeFormatter;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import java.time.LocalDateTime;

import static org.mockito.Mockito.*;

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
        PostResponse response=makePostResponse(LocalDateTime.now(),postId);

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

    @NotNull
    private static String getCreatedDateFormat(PostResponse response) {
        return response.getCreatedAt().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
    }

    @NotNull
    private static String getModifiedDateFormat(PostResponse response) {
        return response.getModifiedAt().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
    }

    private PostResponse makePostResponse(LocalDateTime time, Long postId){
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