package com.midas.shootpointer.domain.backnumber.mapper;

import com.midas.shootpointer.domain.backnumber.dto.BackNumberRequest;
import com.midas.shootpointer.domain.backnumber.dto.BackNumberResponse;
import com.midas.shootpointer.domain.backnumber.entity.BackNumberEntity;

public interface BackNumberMapper {
    BackNumberEntity dtoToEntity(BackNumberRequest request);
    BackNumberResponse entityToDto(BackNumberEntity entity);
}
