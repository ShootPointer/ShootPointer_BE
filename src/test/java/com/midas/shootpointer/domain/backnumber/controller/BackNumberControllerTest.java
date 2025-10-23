package com.midas.shootpointer.domain.backnumber.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.midas.shootpointer.WithMockCustomMember;
import com.midas.shootpointer.domain.backnumber.business.command.BackNumberCommandService;
import com.midas.shootpointer.domain.backnumber.dto.BackNumberRequest;
import com.midas.shootpointer.domain.backnumber.dto.BackNumberResponse;
import com.midas.shootpointer.domain.backnumber.entity.BackNumber;
import com.midas.shootpointer.domain.backnumber.entity.BackNumberEntity;
import com.midas.shootpointer.domain.backnumber.mapper.BackNumberMapper;
import com.midas.shootpointer.domain.member.entity.Member;
import org.junit.jupiter.api.BeforeEach;
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
import org.springframework.web.multipart.MultipartFile;

import static org.mockito.BDDMockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class BackNumberControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private BackNumberMapper mapper;

    @MockitoBean
    private BackNumberCommandService backNumberCommandService;

    @BeforeEach
    void setUp(){
        objectMapper=new ObjectMapper();
    }
    @Test
    @DisplayName("등 번호 POST 요청 성공 시 등 번호 Response를 반환합니다._SUCCESS")
    @WithMockCustomMember
    void create_SUCCESS() throws Exception {
        // given
        String url = "/api/backNumber";
        BackNumberResponse mockResponse = expectedResponse();
        int backNumber=100;

        when(mapper.dtoToEntity(any(BackNumberRequest.class)))
                .thenReturn(BackNumberEntity
                        .builder()
                        .backNumber(BackNumber.of(backNumber))
                        .build()
                );

        given(backNumberCommandService.create(any(Member.class), any(BackNumberEntity.class),any(MultipartFile.class)))
                .willReturn(backNumber);

        BackNumberRequest request = BackNumberRequest.of(100);
        MockMultipartFile jsonPart = new MockMultipartFile(
                "backNumberRequestDto",
                "",
                "application/json",
                objectMapper.writeValueAsBytes(request)
        );



        MockMultipartFile imagePart = new MockMultipartFile(
                "image", "test.img", "image/png", "fake image".getBytes()
        );

        // when & then
        mockMvc.perform(multipart(url)
                        .file(jsonPart)
                        .file(imagePart)
                        .contentType(MediaType.MULTIPART_FORM_DATA))
                .andExpect(status().isOk())
                .andDo(print())
                //.andExpect(jsonPath("$.status").value("CREATED"))
                //.andExpect(jsonPath("$.success").value(true))
                //.andExpect(jsonPath("$.data").value(mockResponse.getBackNumber()))
                ;

        verify(backNumberCommandService).create(any(Member.class), any(BackNumberEntity.class),any(MultipartFile.class));
    }
    /**
     * Mock MultipartFile
     */
    private MockMultipartFile mockMultipartFile(String requestJson){
        return new MockMultipartFile(
                "file",
                "test.img",
                MediaType.APPLICATION_JSON_VALUE,
                requestJson.getBytes()
        );
    }

    /**
     * Mock BackNumberResponse
     */
    private BackNumberResponse expectedResponse(){
        return BackNumberResponse
                .of(100);
    }
}
