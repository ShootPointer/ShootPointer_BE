package com.midas.shootpointer.domain.backnumber.service;

import com.midas.shootpointer.domain.backnumber.dto.BackNumberRequest;
import com.midas.shootpointer.domain.backnumber.dto.BackNumberResponse;
import org.springframework.web.multipart.MultipartFile;

public interface BackNumberService {
    BackNumberResponse create(String token, BackNumberRequest request, MultipartFile image);
}
