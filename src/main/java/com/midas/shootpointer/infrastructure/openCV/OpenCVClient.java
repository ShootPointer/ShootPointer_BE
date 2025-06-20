package com.midas.shootpointer.infrastructure.openCV;

import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.UUID;

public interface OpenCVClient {
    void sendBackNumberInformation(UUID userId, Integer backNumber, MultipartFile image) throws IOException;

}
