package com.midas.shootpointer.infrastructure.openCV.service;

import com.midas.shootpointer.infrastructure.openCV.dto.OpenCVResponse;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.UUID;

public interface OpenCVClient {
    OpenCVResponse<?> sendBackNumberInformation(UUID userId, Integer backNumber, MultipartFile image) throws IOException;

}
