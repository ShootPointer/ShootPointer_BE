package com.midas.shootpointer.domain.like.controller;

import com.midas.shootpointer.WithMockCustomMember;
import com.midas.shootpointer.domain.like.business.command.LikeCommandService;
import com.midas.shootpointer.domain.member.entity.Member;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@WithMockCustomMember
class LikeControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private LikeCommandService likeCommandService;

    private final String baseUrl="/api/like";


    @Test
    @DisplayName("좋아요 생성 POST 요청 성공시 저장된 likeId를 반환합니다._SUCCESS")
    void create_SUCCESS() throws Exception {
        //given
        Long postId=111L;
        //when

        when(likeCommandService.create(anyLong(),any(Member.class)))
                .thenReturn(postId);

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
        Long postId=111L;

        //when
        when(likeCommandService.delete(anyLong(),any(Member.class)))
                .thenReturn(postId);

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