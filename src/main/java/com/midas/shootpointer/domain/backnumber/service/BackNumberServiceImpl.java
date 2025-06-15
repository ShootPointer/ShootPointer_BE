package com.midas.shootpointer.domain.backnumber.service;

import com.midas.shootpointer.domain.backnumber.dto.BackNumberRequest;
import com.midas.shootpointer.domain.backnumber.dto.BackNumberResponse;
import com.midas.shootpointer.domain.backnumber.repository.BackNumberRepository;
import com.midas.shootpointer.global.annotation.CustomLog;
import com.midas.shootpointer.global.util.jwt.JwtUtil;
import com.midas.shootpointer.infrastructure.openCV.OpenCVClient;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class BackNumberServiceImpl implements BackNumberService{
    private final BackNumberRepository repository;
    private final OpenCVClient openCVClient;
    private final JwtUtil jwtUtil;
    @Override
    @Transactional
    @CustomLog
    public BackNumberResponse create(BackNumberRequest request) {
        UUID userID=jwtUtil.getUserId();

        return null;
    }
}
