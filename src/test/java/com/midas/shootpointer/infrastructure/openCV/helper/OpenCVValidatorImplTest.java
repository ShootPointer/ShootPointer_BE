package com.midas.shootpointer.infrastructure.openCV.helper;

import com.midas.shootpointer.global.common.ErrorCode;
import com.midas.shootpointer.global.exception.CustomException;
import com.midas.shootpointer.infrastructure.openCV.dto.OpenCVResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class OpenCVValidatorImplTest {
    @Mock
    private OpenCVResponse response;

    @InjectMocks
    private OpenCVValidatorImpl validator;

    @Test
    @DisplayName("OpenCV의 response의 match값이 false이면 NOT_MATCHED_BACK_NUMBER 예외를 발생시킵니다.")
    void notMathBackNumber_CUSTOM_ERROR(){
        //when
        when(response.getMatch()).thenReturn(false);

        //then
        assertThatThrownBy(()->validator.notMatchBackNumber(response))
                .isInstanceOf(CustomException.class)
                .hasMessage(ErrorCode.NOT_MATCHED_BACK_NUMBER.getMessage());
    }

    @Test
    @DisplayName("OpenCV의 response의 match 값이 true이면 통과시킵니다.")
    void notMathBackNumber(){
        //when
        when(response.getMatch()).thenReturn(true);

        //then
        assertDoesNotThrow(()->validator.notMatchBackNumber(response));
    }

    @Test
    @DisplayName("OpenCV의 response의 success가 false 이면 FAILED_SEND_IMAGE_TO_OPENCV 예외를 발생시킵니다.")
    void failedOpenCVRequest_ERROR_1(){
        //when
        when(response.getSuccess()).thenReturn(false);


        //then
        assertThatThrownBy(()->validator.failedOpenCVRequest(response))
                .isInstanceOf(CustomException.class)
                .hasMessage(ErrorCode.FAILED_SEND_IMAGE_TO_OPENCV.getMessage());
    }

    @Test
    @DisplayName("OpenCV의 response의 status가 200이 아니면 FAILED_SEND_IMAGE_TO_OPENCV 예외를 발생시킵니다.")
    void failedOpenCVRequest_ERROR_2(){
        //when
        when(response.getSuccess()).thenReturn(true);
        when(response.getStatus()).thenReturn(400);

        //then
        assertThatThrownBy(()->validator.failedOpenCVRequest(response))
                .isInstanceOf(CustomException.class)
                .hasMessage(ErrorCode.FAILED_SEND_IMAGE_TO_OPENCV.getMessage());
    }

    @Test
    @DisplayName("OpenCV의 response의 success가 false가 아니고 status가 200이면 통과 시킵니다.")
    void failedOpenCVRequest(){
        //when
        when(response.getStatus()).thenReturn(200);
        when(response.getSuccess()).thenReturn(true);

        //then
        assertDoesNotThrow(()->validator.failedOpenCVRequest(response));
    }
}