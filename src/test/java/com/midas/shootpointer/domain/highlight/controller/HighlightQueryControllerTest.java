package com.midas.shootpointer.domain.highlight.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.midas.shootpointer.WithMockCustomMember;
import com.midas.shootpointer.domain.highlight.business.HighlightManager;
import com.midas.shootpointer.domain.highlight.dto.HighlightInfoResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class HighlightQueryControllerTest {
    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    @InjectMocks
    private HighlightQueryController highlightQueryController;

    @Mock
    private HighlightManager highlightManager;

    @BeforeEach
    void setUp(){
        mockMvc= MockMvcBuilders.standaloneSetup(highlightQueryController)
                .build();
        objectMapper=new ObjectMapper();
    }

    @Test
    @DisplayName("특정 유저의 하이라이트 영상 리스트 조회 시 Page<HighlightInfoResponse>를 반환합니다.")
    @WithMockCustomMember(name = "test",email = "test@naver.com")
    void highlightList() throws Exception {
        // given
        String url = "/api/highlight/list";

        UUID highlightId1 = UUID.randomUUID();
        UUID highlightId2 = UUID.randomUUID();
        UUID highlightId3 = UUID.randomUUID();
        UUID highlightId4 = UUID.randomUUID();

        LocalDateTime now = LocalDateTime.now();

        HighlightInfoResponse response1 = HighlightInfoResponse.of(highlightId1, now, 20, 30,"highlight1.mp4.");
        HighlightInfoResponse response2 = HighlightInfoResponse.of(highlightId2, now.minusDays(1), 10, 40,"highlight2.mp4");
        HighlightInfoResponse response3 = HighlightInfoResponse.of(highlightId3, now.minusDays(2), 10, 40,"highlight3.mp4");
        HighlightInfoResponse response4 = HighlightInfoResponse.of(highlightId4, now.minusDays(3), 10, 40,"highlight4.mp4");

        Page<HighlightInfoResponse> expected =
                new PageImpl<>(List.of(response1, response2,response3,response4));

        when(highlightManager.listByPaging(anyInt(), anyInt(), any(UUID.class)))
                .thenReturn(expected);

        // when & then
        mockMvc.perform(get(url)
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("OK"))
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.content").isArray())
                .andExpect(jsonPath("$.data.content[0].highlightId").value(highlightId1.toString()))
                .andExpect(jsonPath("$.data.content[1].highlightId").value(highlightId2.toString()))
                .andExpect(jsonPath("$.data.content[2].highlightId").value(highlightId3.toString()))
                .andExpect(jsonPath("$.data.content[3].highlightId").value(highlightId4.toString()))
                .andExpect(jsonPath("$.data.content.length()").value(4))

                .andExpect(jsonPath("$.data.size").value(4))
                .andExpect(jsonPath("$.data.totalElements").value(4))
                .andExpect(jsonPath("$.data.totalPages").value(1))
                .andDo(print());
    }
}