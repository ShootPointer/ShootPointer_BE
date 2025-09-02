package com.midas.shootpointer.domain.like.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.midas.shootpointer.domain.like.business.command.LikeCommandService;
import com.midas.shootpointer.domain.member.entity.Member;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class LikeControllerTest {
    private MockMvc mockMvc;

    @InjectMocks
    private LikeController likeController;

    @Mock
    private LikeCommandService likeCommandService;

    private final String baseUrl="/api/like";

    @BeforeEach
    void setUp(){
        mockMvc= MockMvcBuilders.standaloneSetup(likeController)
                .build();
    }

    @Test
    @DisplayName("좋아요 생성 POST 요청 성공시 저장된 likeId를 반환합니다._SUCCESS")
    void create_SUCCESS() throws Exception {
        //given
        String postId="111";

        //when
        when(likeCommandService.create(anyLong(),any(Member.class)))
                .thenReturn(Long.parseLong(postId));

        //then
        mockMvc.perform(post(baseUrl+"/"+postId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("CREATED"))
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data").value(111L))
                .andDo(print());

        verify(likeCommandService,times(1)).create(anyLong(),any(Member.class));
    }

    @Test
    @DisplayName("좋아요 삭제 DELETE 요청 성공시 삭제된 likeId를 반환합니다._SUCCESS")
    void delete_SUCCESS() throws Exception {
        //given
        String postId="111";

        //when
        when(likeCommandService.delete(anyLong(),any(Member.class)))
                .thenReturn(Long.parseLong(postId));

        //then
        mockMvc.perform(delete(baseUrl+"/"+postId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("OK"))
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data").value(111L))
                .andDo(print());

        verify(likeCommandService,times(1)).delete(anyLong(),any(Member.class));
    }
}