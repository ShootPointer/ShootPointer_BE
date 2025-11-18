package com.midas.shootpointer.domain.highlight.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.midas.shootpointer.WithMockCustomMember;
import com.midas.shootpointer.domain.highlight.business.HighlightManager;
import com.midas.shootpointer.domain.highlight.dto.HighlightSelectResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.UUID;
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




    private HighlightSelectResponse mockHighlightSelectResponse(List<UUID> uuids){
        return HighlightSelectResponse.builder()
                .selectedHighlightIds(uuids)
                .build();
    }
}
