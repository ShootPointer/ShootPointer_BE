package com.midas.shootpointer.domain.highlight.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.midas.shootpointer.WithMockCustomMember;
import com.midas.shootpointer.domain.highlight.business.HighlightManager;
import com.midas.shootpointer.domain.highlight.dto.HighlightSelectRequest;
import com.midas.shootpointer.domain.highlight.dto.HighlightSelectResponse;
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

import java.util.List;
import java.util.UUID;

import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
@AutoConfigureMockMvc
@SpringBootTest
@ActiveProfiles("test")
@WithMockCustomMember
class HighlightCommandControllerTest  {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private HighlightManager manager;


    @Test
    @DisplayName("하이라이트 영상 선택 POST 요청 성공시 HighlightSelectResponse를 반환합니다._SUCCESS")
    void selectHighlight() throws Exception {
        //given
        String url="/api/highlight/select";

        UUID highlight1=UUID.randomUUID();
        UUID highlight2=UUID.randomUUID();

        List<UUID> uuids=List.of(highlight1,highlight2);
        HighlightSelectResponse expectedResponse=mockHighlightSelectResponse(uuids);
        HighlightSelectRequest request=mockHighlightSelectRequest(uuids);

        when(manager.selectHighlight(any(HighlightSelectRequest.class),any(Member.class)))
                .thenReturn(expectedResponse);

        //when & then
        mockMvc.perform(post(url)
                        .content(objectMapper.writeValueAsString(request))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("OK"))
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.selectedHighlightIds",containsInAnyOrder(
                        highlight1.toString(),
                        highlight2.toString()
                )))
                .andDo(print());

        verify(manager).selectHighlight(any(HighlightSelectRequest.class),any(Member.class));
    }




    private HighlightSelectResponse mockHighlightSelectResponse(List<UUID> uuids){
        return HighlightSelectResponse.builder()
                .selectedHighlightIds(uuids)
                .build();
    }

    /*
     * Mock HighlightSelectRequest
     */

    private HighlightSelectRequest mockHighlightSelectRequest(List<UUID> uuids){
        return HighlightSelectRequest.builder()
                .selectedHighlightIds(uuids)
                .build();
    }
}
