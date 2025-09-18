package com.midas.shootpointer.domain.backnumber.helper;

import com.midas.shootpointer.domain.backnumber.entity.BackNumber;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class BackNumberHelperImplTest {
    @InjectMocks
    private BackNumberHelperImpl backNumberHelper;

    @Mock
    private BackNumberUtil backNumberUtil;

    @Test
    @DisplayName("BackNumber 객체로 DB에 존재하는 객체인지 확인하고 불러옵니다. 존재하지 않으면 생성 후 DB에 저장합니다. - backNumberUtil.findOrElseGetBackNumber(backNumber)의 실행여부를 확인합니다.")
    void findOrElseGetBackNumber(){
        //given
        BackNumber backNumber=BackNumber.of(10);

        //when
        backNumberHelper.findOrElseGetBackNumber(backNumber);

        //then
        verify(backNumberUtil, times(1)).findOrElseGetBackNumber(backNumber);
    }
}