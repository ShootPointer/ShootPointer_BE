package com.midas.shootpointer.infrastructure.openCV.helper;

import com.midas.shootpointer.global.common.ErrorCode;
import com.midas.shootpointer.global.exception.CustomException;
import com.midas.shootpointer.infrastructure.openCV.dto.OpenCVResponse;
import org.springframework.stereotype.Component;

@Component
public class OpenCVValidatorImpl implements OpenCVValidator{
    /**
     * 클라이언트가 보낸 등번호와 openCV 추출 등 번호가 일치하지 않을 때
     * @param response openCV 응답값
     */
    @Override
    public void notMatchBackNumber(OpenCVResponse response) {
        if (!response.getMatch()){
            throw new CustomException(ErrorCode.NOT_MATCHED_BACK_NUMBER);
        }
    }

    /**
     * openCV 와의 통신 실패 시
     * @param response openCv 응답값
     */
    @Override
    public void failedOpenCVRequest(OpenCVResponse response) {
        if (!response.getStatus().equals(200)){
            throw new CustomException(ErrorCode.FAILED_SEND_IMAGE_TO_OPENCV);
        }
    }
}
