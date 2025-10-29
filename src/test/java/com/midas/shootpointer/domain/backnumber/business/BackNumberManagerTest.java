package com.midas.shootpointer.domain.backnumber.business;

import com.midas.shootpointer.domain.backnumber.entity.BackNumber;
import com.midas.shootpointer.domain.backnumber.entity.BackNumberEntity;
import com.midas.shootpointer.domain.backnumber.repository.BackNumberRepository;
import com.midas.shootpointer.domain.member.entity.Member;
import com.midas.shootpointer.domain.member.repository.MemberCommandRepository;
import com.midas.shootpointer.domain.memberbacknumber.repository.MemberBackNumberRepository;
import com.midas.shootpointer.infrastructure.openCV.service.OpenCVClient;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.io.IOException;
import java.util.UUID;

import static org.mockito.Mockito.*;

/**
 * 통합 테스트로 진행
 */
@SpringBootTest
@ActiveProfiles("dev")
class BackNumberManagerTest  {
    @Autowired
    private BackNumberManager backNumberManager;

    @Autowired
    private BackNumberRepository backNumberRepository;

    @Autowired
    private MemberCommandRepository memberCommandRepository;

    @Autowired
    private MemberBackNumberRepository memberBackNumberRepository;

    @MockitoBean
    private OpenCVClient openCVClient;

    private Member member;

    @BeforeEach
    void setUp() {
        member = memberCommandRepository.save(makeMockMember());
    }

    @AfterEach
    void cleanUp() {
        memberBackNumberRepository.deleteAll();
        memberCommandRepository.deleteAll();
        backNumberRepository.deleteAll();
    }

    @Test
    @DisplayName("유저가 요청한 등번호에 대해 연관관계를 매핑하여 저장하고 OpenCv에 사진을 전송합니다.")
    void create() throws IOException {
        //given
        BackNumber backNumber = BackNumber.of(100);
        BackNumberEntity backNumberEntity = BackNumberEntity.builder().backNumber(backNumber).build();

        MockMultipartFile mockMultipartFile = new MockMultipartFile(
                "file",
                "test.img",
                "image/png",
                "fake img".getBytes()
        );

        //when
        /*when(openCVClient.sendBackNumberInformation(any(UUID.class), anyInt(), any(MultipartFile.class)))
                .thenAnswer(invocation -> OpenCVResponse.builder()
                        .data(List.of(mockMultipartFile))
                        .status(100)
                        .success(true)
                        .build()
                );
*/
        int resultBackNumber = backNumberManager.create(backNumberEntity, mockMultipartFile, member);

        //then
        Assertions.assertThat(resultBackNumber).isEqualTo(100);
        verify(openCVClient, times(1))
                .sendBackNumberInformation(any(UUID.class), anyInt(), any());
    }

    private static Member makeMockMember() {
        return Member.builder()
                .username("user")
                .email("test@naver.com")
                .build();
    }

}