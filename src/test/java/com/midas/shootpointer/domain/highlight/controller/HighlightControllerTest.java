package com.midas.shootpointer.domain.highlight.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.midas.shootpointer.BaseSpringBootTest;
import com.midas.shootpointer.WithMockCustomMember;
import com.midas.shootpointer.domain.highlight.business.command.HighlightCommandService;
import com.midas.shootpointer.domain.highlight.dto.HighlightResponse;
import com.midas.shootpointer.domain.highlight.dto.HighlightSelectRequest;
import com.midas.shootpointer.domain.highlight.dto.HighlightSelectResponse;
import com.midas.shootpointer.domain.highlight.dto.UploadHighlight;
import com.midas.shootpointer.domain.member.entity.Member;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
@AutoConfigureMockMvc
@SpringBootTest
@ActiveProfiles("test")
@WithMockCustomMember
class HighlightControllerTest extends BaseSpringBootTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private HighlightCommandService highlightCommandService;


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

        when(highlightCommandService.selectHighlight(any(HighlightSelectRequest.class),any(Member.class)))
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

        verify(highlightCommandService).selectHighlight(any(HighlightSelectRequest.class),any(Member.class));
    }


    @Test
    @DisplayName("생성된 하이라이트 영상을 저장하고 성공시 List<HighlightResponse>를 반환합니다.._SUCCESS")
    void uploadHighlights() throws Exception {
        //given
        String url="/api/highlight/upload-result";

        UUID memberId=UUID.randomUUID();
        UUID highlightKey=UUID.randomUUID();
        UUID highlightId1=UUID.randomUUID();
        UUID highlightId2=UUID.randomUUID();

        LocalDateTime time=LocalDateTime.now();

        UploadHighlight request=UploadHighlight.builder()
                        .createAt(LocalDateTime.now())
                        .highlightKey(highlightKey.toString())
                        .build();

        byte[] content=new byte[10];
        MockMultipartFile video1=new MockMultipartFile("highlights","video1.mp4","video/mp4",content);
        MockMultipartFile video2=new MockMultipartFile("highlights","video2.mp4","video/mp4",content);
        MockMultipartFile dto=new MockMultipartFile(
                "uploadHighlightDto","",
                "application/json",
                objectMapper.writeValueAsBytes(request)
        );

        List<HighlightResponse> expectedResponse=List.of(
                HighlightResponse.builder()
                        .createdAt(time)
                        .highlightId(highlightId1)
                        .highlightIdentifier(highlightKey)
                        .highlightUrl("url")
                        .build(),
                HighlightResponse.builder()
                        .createdAt(time)
                        .highlightId(highlightId2)
                        .highlightIdentifier(highlightKey)
                        .highlightUrl("url")
                        .build()
        );

        when(highlightCommandService.uploadHighlights(any(UploadHighlight.class),anyList(),any(UUID.class)))
                .thenReturn(expectedResponse);

        //when & then
        mockMvc.perform(multipart(url)
                        .file(video1)
                        .file(video2)
                        .file(dto)
                        .header("X-Member-Id",memberId.toString())
                        .contentType(MediaType.MULTIPART_FORM_DATA))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("OK"))
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data[0].highlightId").value(highlightId1.toString()))
                .andExpect(jsonPath("$.data[0].highlightIdentifier").value(highlightKey.toString()))
                .andExpect(jsonPath("$.data[0].highlightUrl").value("url"))

                .andExpect(jsonPath("$.data[1].highlightId").value(highlightId2.toString()))
                .andExpect(jsonPath("$.data[1].highlightIdentifier").value(highlightKey.toString()))
                .andExpect(jsonPath("$.data[1].highlightUrl").value("url"))
                .andDo(print());

        verify(highlightCommandService).uploadHighlights(any(UploadHighlight.class),anyList(),any(UUID.class));
    }
    /*
     * Mock HighlightSelectResponse
     */

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
