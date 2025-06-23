package com.midas.shootpointer.domain.backnumber.mapper;

import com.midas.shootpointer.domain.backnumber.dto.BackNumberRequest;
import com.midas.shootpointer.domain.backnumber.dto.BackNumberResponse;
import com.midas.shootpointer.domain.backnumber.entity.BackNumber;
import com.midas.shootpointer.domain.backnumber.entity.BackNumberEntity;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockMultipartFile;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
@SpringBootTest
class BackNumberMapperImplTest {

    @Autowired
    private BackNumberMapperImpl backNumberMapper;
    @Test
    @DisplayName("DTO를 Entity로 매핑 합니다.")
    void dtoToEntity_SUCCESS(){
        //given
        Integer mockBackNumber=1;
        MockMultipartFile mockMultipartFile=new MockMultipartFile(
                "file",
                "test.img",
                "image/png",
                "fake img".getBytes()
        );
        BackNumberRequest requestDto=BackNumberRequest.of(mockBackNumber);

        //when
        BackNumberEntity backNumberEntity=backNumberMapper.dtoToEntity(requestDto);

        //then
        assertThat(backNumberEntity.getBackNumber().getNumber())
                .isEqualTo(1);
    }

    @Test
    @DisplayName("Entity를 DTO로 매핑합니다.")
    void EntityToDto_SUCCESS(){
        //given
        BackNumberEntity backNumber=BackNumberEntity
                .builder()
                .backNumber(BackNumber.of(1))
                .build();

        //when
        BackNumberResponse backNumberResponse=backNumberMapper.entityToDto(backNumber);

        //then
        assertThat(backNumberResponse.getBackNumber())
                .isEqualTo(1);
    }

}