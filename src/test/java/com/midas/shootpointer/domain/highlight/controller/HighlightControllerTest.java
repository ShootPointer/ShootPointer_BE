package com.midas.shootpointer.domain.highlight.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.midas.shootpointer.domain.highlight.dto.HighlightSelectRequest;
import com.midas.shootpointer.domain.highlight.dto.HighlightSelectResponse;
import com.midas.shootpointer.domain.highlight.service.command.HighlightCommandService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import static org.hamcrest.Matchers.containsInAnyOrder;
import org.springframework.test.web.servlet.MockMvcBuilder;
import org.springframework.test.web.servlet.assertj.MockMvcTester;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class HighlightControllerTest {
    private MockMvc mockMvc;

    private ObjectMapper objectMapper;
    @Mock
    private HighlightCommandService highlightCommandService;

    @InjectMocks
    private HighlightController highlightController;

    @BeforeEach
    void setUp(){
        mockMvc= MockMvcBuilders.standaloneSetup(highlightController).build();
        objectMapper=new ObjectMapper();
    }

    @Test
    @DisplayName("하이라이트 영상 선택 POST 요청 성공시 HighlightSelectResponse를 반환합니다._SUCCESS")
    void selectHighlight() throws Exception {
        //given
        String url="/api/highlight/select";
        String token="test-token";

        UUID highlight1=UUID.randomUUID();
        UUID highlight2=UUID.randomUUID();

        List<UUID> uuids=List.of(highlight1,highlight2);
        HighlightSelectResponse expectedResponse=mockHighlightSelectResponse(uuids);
        HighlightSelectRequest request=mockHighlightSelectRequest(uuids);

        when(highlightCommandService.selectHighlight(any(HighlightSelectRequest.class),any(String.class)))
                .thenReturn(expectedResponse);

        //when & then
        mockMvc.perform(post(url)
                        .content(objectMapper.writeValueAsString(request))
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("OK"))
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.selectedHighlightIds",containsInAnyOrder(
                        highlight1.toString(),
                        highlight2.toString()
                )))
                .andDo(print());

        verify(highlightCommandService).selectHighlight(any(HighlightSelectRequest.class),any(String.class));
    }

    /**
     * Mock HighlightSelectResponse
     */
    private HighlightSelectResponse mockHighlightSelectResponse(List<UUID> uuids){
        return HighlightSelectResponse.builder()
                .selectedHighlightIds(uuids)
                .build();
    }

    /**
     * Mock HighlightSelectRequest
     */
    private HighlightSelectRequest mockHighlightSelectRequest(List<UUID> uuids){
        return HighlightSelectRequest.builder()
                .selectedHighlightIds(uuids)
                .build();
    }
}