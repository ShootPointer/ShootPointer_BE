package com.midas.shootpointer.domain.post.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.midas.shootpointer.domain.member.entity.Member;
import com.midas.shootpointer.domain.post.business.command.PostCommandService;
import com.midas.shootpointer.domain.post.dto.request.PostRequest;
import com.midas.shootpointer.domain.post.entity.HashTag;
import com.midas.shootpointer.domain.post.entity.PostEntity;
import com.midas.shootpointer.domain.post.mapper.PostMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import java.util.UUID;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;

@ExtendWith(MockitoExtension.class)
class PostCommandControllerTest {
    private MockMvc mockMvc;

    private ObjectMapper objectMapper;

    @InjectMocks
    private PostCommandController postCommandController;

    @Mock
    private PostCommandService postCommandService;

    @Mock
    private PostMapper postMapper;

    private final String baseUrl="/api/post";

    @BeforeEach
    void setUp(){
        mockMvc= MockMvcBuilders.standaloneSetup(postCommandController)
                .build();
        objectMapper=new ObjectMapper();
    }
    @Test
    @DisplayName("게시물 직상 POST 요청 성공시 저장된 postId를 반환합니다._SUCCESS")
    void create_SUCCESS() throws Exception {
        //given
        Long savedPostId=111L;
        PostRequest postRequest=mockPostRequest();
        PostEntity post=mockPost();

        //when
        when(postMapper.dtoToEntity(any(PostRequest.class),any(Member.class))).thenReturn(post);
        when(postCommandService.create(any(PostEntity.class),any(Member.class)))
                        .thenReturn(savedPostId);

        //then
        mockMvc.perform(post(baseUrl)
                .content(objectMapper.writeValueAsString(postRequest))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("CREATED"))
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data").value(111L))
                .andDo(print());

        verify(postCommandService,times(1)).create(any(PostEntity.class),any(Member.class));
    }

    @Test
    @DisplayName("게시물 수정 PUT 요청 성공시 수정된 postId를 반환합니다._SUCCESS")
    void update_SUCCESS() throws Exception {
        //given
        String postId="111";
        PostRequest postRequest=mockPostRequest();
        PostEntity post=mockPost();
        //when
        when(postMapper.dtoToEntity(any(PostRequest.class),any(Member.class))).thenReturn(post);
        when(postCommandService.update(any(PostEntity.class),any(Member.class),anyLong()))
                .thenReturn(Long.decode(postId));

        //then
        mockMvc.perform(put(baseUrl+"/"+postId)
                        .content(objectMapper.writeValueAsString(postRequest))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("OK"))
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data").value(111L))
                .andDo(print());

        verify(postCommandService,times(1)).update(any(PostEntity.class),any(Member.class),anyLong());
    }



    @Test
    @DisplayName("게시물 삭제 DELETE 요청 성공시 삭제된 postId를 반환합니다._SUCCESS")
    void delete_SUCCESS() throws Exception {
        //given
        String postId="111";

        //when
        when(postCommandService.delete(any(Member.class),anyLong()))
                .thenReturn(Long.parseLong(postId));

        //then
        mockMvc.perform(delete(baseUrl+"/"+postId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("OK"))
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data").value(111L))
                .andDo(print());

        verify(postCommandService,times(1)).delete(any(Member.class),anyLong());
    }
    /**
     * mock PostRequest
     */
    private PostRequest mockPostRequest() {
        return PostRequest.of(UUID.randomUUID(), "title", "content", HashTag.TREE_POINT);
    }

    /**
     * Mock Member
     */
    private Member mockMember() {
        return Member.builder()
                .memberId(UUID.randomUUID())
                .email("test@naver.com")
                .username("test")
                .build();
    }

    /**
     * Mock PostEntity
     */
    private PostEntity mockPost(){
        return PostEntity.builder()
                .title("title")
                .content("content")
                .hashTag(HashTag.TREE_POINT)
                .build();
    }
}