package com.midas.shootpointer.infrastructure.openCV.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.midas.shootpointer.domain.member.repository.MemberRepository;
import com.midas.shootpointer.global.util.jwt.JwtUtil;
import com.midas.shootpointer.infrastructure.openCV.OpenCVProperties;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MockMvcBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class OpenCVClientControllerTest {

    @Mock
    private OpenCVProperties openCVProperties;

    @Mock
    private JwtUtil jwtUtil;

    @Test
    @DisplayName("영상 업로드 API 요청 시 Proxy Server 주소를 반환합니다._SUCCESS")
    void getUploadUrl() throws Exception {
        //given
        OpenCVProperties.Api api = new OpenCVProperties.Api();
        OpenCVProperties.Api.Proxy proxy = new OpenCVProperties.Api.Proxy();
        proxy.setUploadVideo("upload");
        api.setProxy(proxy);
        String mockToken = "Bearer testToken";
        UUID mockUUID=UUID.randomUUID();

        when(jwtUtil.getUserId()).thenReturn(mockUUID);
        when(openCVProperties.getUrl()).thenReturn("http://localhost:8888");
        when(openCVProperties.getApi()).thenReturn(api);

        OpenCVClientController controller = new OpenCVClientController(openCVProperties,jwtUtil);
        MockMvc mockMvc = MockMvcBuilders.standaloneSetup(controller).build();

        //when & then
        mockMvc.perform(get("/api/video/upload")
                        .header("Authorization", mockToken)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(Boolean.TRUE))
                .andExpect(jsonPath("$.data").value("http://localhost:8888/upload?memberId="+mockUUID));
    }
}