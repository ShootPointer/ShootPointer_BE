package com.midas.shootpointer.domain.backnumber.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.midas.shootpointer.domain.backnumber.dto.BackNumberRequest;
import com.midas.shootpointer.domain.backnumber.dto.BackNumberResponse;
import com.midas.shootpointer.domain.backnumber.service.BackNumberService;
import com.midas.shootpointer.global.dto.ApiResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

import static org.mockito.BDDMockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class BackNumberControllerTest {


    private MockMvc mockMvc;

    private ObjectMapper objectMapper;

    @Mock
    private BackNumberService backNumberService;

    @InjectMocks
    private BackNumberController backNumberController;

    @BeforeEach
    void setUp(){
        mockMvc= MockMvcBuilders.standaloneSetup(backNumberController)
                .build();
        objectMapper=new ObjectMapper();
    }
    @Test
    @DisplayName("등 번호 POST 요청 성공 시 등 번호 Response를 반환합니다._SUCCESS")
    void create_SUCCESS() throws Exception {
        // given
        String url = "/api/backNumber";
        BackNumberResponse mockResponse = expectedResponse();

        given(backNumberService.create(anyString(), any(BackNumberRequest.class),any(MultipartFile.class)))
                .willReturn(mockResponse);

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
                        .header("Authorization", "Bearer fake-jwt-token")
                        .contentType(MediaType.MULTIPART_FORM_DATA))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("CREATED"))
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.backNumber").value(mockResponse.getBackNumber()))
                .andDo(print());

        verify(backNumberService).create(anyString(), any(BackNumberRequest.class),any(MultipartFile.class));
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
