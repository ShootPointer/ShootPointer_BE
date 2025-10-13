package com.midas.shootpointer.infrastructure.openCV.service;

import com.midas.shootpointer.infrastructure.openCV.dto.OpenCVResponse;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

public interface OpenCVClient {
    OpenCVResponse sendBackNumberInformation(UUID memberId, Integer backNumber, MultipartFile image);

}
