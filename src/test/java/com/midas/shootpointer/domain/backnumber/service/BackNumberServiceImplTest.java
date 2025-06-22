package com.midas.shootpointer.domain.backnumber.service;

import com.midas.shootpointer.domain.backnumber.dto.BackNumberRequest;
import com.midas.shootpointer.domain.backnumber.dto.BackNumberResponse;
import com.midas.shootpointer.domain.backnumber.entity.BackNumber;
import com.midas.shootpointer.domain.backnumber.entity.BackNumberEntity;
import com.midas.shootpointer.domain.backnumber.mapper.BackNumberMapper;
import com.midas.shootpointer.domain.backnumber.repository.BackNumberRepository;
import com.midas.shootpointer.domain.member.entity.Member;
import com.midas.shootpointer.domain.member.repository.MemberRepository;
import com.midas.shootpointer.domain.memberbacknumber.repository.MemberBackNumberRepository;
import com.midas.shootpointer.global.util.jwt.JwtUtil;
import com.midas.shootpointer.infrastructure.openCV.service.OpenCVClient;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;

import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;

import java.io.IOException;
import java.util.Optional;
import java.util.UUID;

import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
class BackNumberServiceImplTest {

    @InjectMocks
    private BackNumberServiceImpl backNumberService;
    @Mock
    private BackNumberMapper backNumberMapper;

    @Mock
    private BackNumberRepository backNumberRepository;

    @Mock
    private MemberBackNumberRepository memberBackNumberRepository;

    @Mock
    private MemberRepository memberRepository;

    @Mock
    private OpenCVClient openCVClient;

    @Mock
    private JwtUtil jwtUtil;

    @DisplayName("등 번호 이미지를 OpenCVClient를 통해 openCV서버로 전송하고 회원의 등 번호와 회원 정보를 저장합니다._SUCCESS " +
            "case1 : 등 번호가 이미 존재 하는 경우")
    @Test
    void create_existed_backNumber_SUCCESS() throws IOException {
        //given
        BackNumberRequest request = mockBackNumberRequest();
        UUID mockUserId = UUID.randomUUID();
        Member mockMember = mockMember();
        BackNumber mockBackNumber = BackNumber.of(request.getBackNumber());
        BackNumberEntity mockBackNumberEntity = mockBackNumberEntity(mockBackNumber);
        BackNumberResponse expectedResponse = BackNumberResponse.of(100);

        when(jwtUtil.getUserId())
                .thenReturn(mockUserId);
        when(memberRepository.findByMemberId(1L))
                .thenReturn(Optional.of(mockMember));
        when(backNumberRepository.findByBackNumber(mockBackNumber))
                .thenReturn(Optional.of(mockBackNumberEntity));
        when(backNumberMapper.entityToDto(mockBackNumberEntity))
                .thenReturn(expectedResponse);

        //when
        BackNumberResponse response = backNumberService.create(request);

        //then
        assertThat(response).isNotNull();
        assertThat(response.getBackNumber()).isEqualTo(100);

        verify(openCVClient).sendBackNumberInformation(eq(mockUserId)
                , eq(100),
                any()
        );
        verify(memberBackNumberRepository).save(any());
    }

    @DisplayName("등 번호 이미지를 OpenCVClient를 통해 openCV서버로 전송하고 회원의 등 번호와 회원 정보를 저장합니다._SUCCESS " +
            "case2 : 등 번호가 존재하지 않는 경우")
    @Test
    void create_no_existed_backNumber_SUCCESS() throws IOException {
        //given
        BackNumberRequest request = mockBackNumberRequest();
        UUID mockUserId = UUID.randomUUID();
        Member mockMember = mockMember();
        BackNumber mockBackNumber = BackNumber.of(request.getBackNumber());
        BackNumberEntity mockBackNumberEntity = mockBackNumberEntity(mockBackNumber);
        BackNumberResponse expectedResponse = BackNumberResponse.of(100);

        when(jwtUtil.getUserId())
                .thenReturn(mockUserId);
        when(memberRepository.findByMemberId(1L))
                .thenReturn(Optional.of(mockMember));
        //등 번호가 존재하지 않음
        when(backNumberRepository.findByBackNumber(mockBackNumber))
                .thenReturn(Optional.empty());
        when(backNumberMapper.dtoToEntity(request))
                .thenReturn(mockBackNumberEntity);
        when(backNumberRepository.save(mockBackNumberEntity))
                .thenReturn(mockBackNumberEntity);

        when(backNumberMapper.entityToDto(mockBackNumberEntity))
                .thenReturn(expectedResponse);

        //when
        BackNumberResponse response = backNumberService.create(request);

        //then
        assertThat(response).isNotNull();
        assertThat(response.getBackNumber()).isEqualTo(100);

        verify(openCVClient).sendBackNumberInformation(eq(mockUserId)
                , eq(100),
                any()
        );
        verify(memberBackNumberRepository).save(any());
    }
    /**
     * Mock Member
     */
    private Member mockMember() {
        return Member.builder()
                .memberId(1L)
                .email("test@naver.com")
                .username("test")
                .build();
    }

    /**
     * Mock BackNumber Request
     */
    private BackNumberRequest mockBackNumberRequest() {
        return BackNumberRequest.of(100, mockMultipartFile());
    }

    /**
     * Mock MultiFile
     */
    private MockMultipartFile mockMultipartFile() {
        return new MockMultipartFile(
                "file",
                "test.img",
                "image/png",
                "fake img".getBytes()
        );
    }

    /**
     * Mock BackNumberEntity
     */
    private BackNumberEntity mockBackNumberEntity(BackNumber backNumber) {
        return BackNumberEntity
                .builder()
                .backNumberId(1L)
                .backNumber(backNumber)
                .build();
    }
}