package com.midas.shootpointer.domain.backnumber.helper;

import com.midas.shootpointer.BaseSpringBootTest;
import com.midas.shootpointer.domain.backnumber.entity.BackNumber;
import com.midas.shootpointer.domain.backnumber.entity.BackNumberEntity;
import com.midas.shootpointer.domain.backnumber.repository.BackNumberRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import static org.assertj.core.api.Assertions.assertThat;

@ActiveProfiles("dev")
@SpringBootTest
class BackNumberUtilImplTest extends BaseSpringBootTest {
    @Autowired
    private BackNumberUtilImpl backNumberUtil;

    @Autowired
    private BackNumberRepository backNumberRepository;

    @AfterEach
    void cleanUp(){
        backNumberRepository.deleteAll();
    }

    @Test
    @DisplayName("BackNumber 객체로 entity를 조회합니다.-존재하는 경우(조회)")
    void findOrElseGetBackNumber_EXIST(){
        //given
        int number=1234;
        BackNumber backNumber=BackNumber.of(number);
        BackNumberEntity backNumberEntity=BackNumberEntity.builder()
                .backNumber(backNumber)
                .build();
        backNumberEntity=backNumberRepository.save(backNumberEntity);

        //when
        BackNumberEntity findBackNumberEntity=backNumberUtil.findOrElseGetBackNumber(backNumber);

        //then
        assertThat(findBackNumberEntity.getBackNumber()).isEqualTo(backNumberEntity.getBackNumber());
        assertThat(findBackNumberEntity.getBackNumberId()).isEqualTo(backNumberEntity.getBackNumberId());
    }

    @Test
    @DisplayName("BackNumber 객체로 entity를 조회하고 생성합니다.-존재하지 않는 경우(생성 및 저장)")
    void findOrElseGetBackNumber_NOT_EXIST(){
        //given
        int number=1234;
        BackNumber backNumber=BackNumber.of(number);

        //when
        BackNumberEntity saveBackNumberEntity=backNumberUtil.findOrElseGetBackNumber(backNumber);

        //then
        assertThat(saveBackNumberEntity.getBackNumber()).isEqualTo(backNumber);
    }
}