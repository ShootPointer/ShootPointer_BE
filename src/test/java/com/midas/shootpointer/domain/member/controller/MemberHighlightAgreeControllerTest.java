package com.midas.shootpointer.domain.member.controller;

import com.midas.shootpointer.WithMockCustomMember;
import com.midas.shootpointer.domain.member.business.command.MemberCommandService;
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
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
@ActiveProfiles("test")
@WithMockCustomMember
class MemberHighlightAgreeControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private MemberCommandService memberCommandService;

    @Test
    @DisplayName("회원의 하이라이트 정보 집계동의")
    void agree() throws Exception {
        //given
        UUID memberId=UUID.randomUUID();

        //when
        when(memberCommandService.agree(any(Member.class))).thenReturn(memberId);

        //then
        mockMvc.perform(put("/agree/highlight")
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));

        verify(memberCommandService,times(1)).agree(any(Member.class));
    }
}