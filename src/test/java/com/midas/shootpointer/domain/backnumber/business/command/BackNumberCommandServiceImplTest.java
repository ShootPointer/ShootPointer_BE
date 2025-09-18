package com.midas.shootpointer.domain.backnumber.business.command;

import com.midas.shootpointer.domain.backnumber.business.BackNumberManager;
import com.midas.shootpointer.domain.backnumber.entity.BackNumber;
import com.midas.shootpointer.domain.backnumber.entity.BackNumberEntity;
import com.midas.shootpointer.domain.member.entity.Member;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import java.nio.charset.StandardCharsets;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class BackNumberCommandServiceImplTest {
    @InjectMocks
    private BackNumberCommandServiceImpl backNumberCommandService;

    @Mock
    private BackNumberManager backNumberManager;

    @Test
    @DisplayName("등 번호를 등록합니다. - backNumberManager.create(backNumberEntity,image,member) 실행 여부를 확인합니다.")
    void create(){
        //given
        BackNumberEntity backNumberEntity=BackNumberEntity.builder()
                .backNumber(BackNumber.of(10))
                        .build();
        Member member=Member.builder().build();
        MultipartFile multipartFile= new MockMultipartFile(
                "file",
                "test.img",
                MediaType.APPLICATION_JSON_VALUE,
                "fake image".getBytes(StandardCharsets.UTF_8)
        );


        //when
        backNumberCommandService.create(member,backNumberEntity,multipartFile);

        //then
        verify(backNumberManager, times(1)).create(backNumberEntity,multipartFile,member);
    }
}