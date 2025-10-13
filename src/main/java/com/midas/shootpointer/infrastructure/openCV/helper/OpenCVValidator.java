package com.midas.shootpointer.infrastructure.openCV.helper;

import com.midas.shootpointer.infrastructure.openCV.dto.OpenCVResponse;

public interface OpenCVValidator {
    void notMatchBackNumber(OpenCVResponse response);
    void failedOpenCVRequest(OpenCVResponse response);

}
