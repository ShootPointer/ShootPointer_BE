package com.midas.shootpointer.domain.backnumber.service;

import com.midas.shootpointer.domain.backnumber.dto.BackNumberRequest;
import com.midas.shootpointer.domain.backnumber.dto.BackNumberResponse;

public interface BackNumberService {
    BackNumberResponse create(BackNumberRequest request);
}
