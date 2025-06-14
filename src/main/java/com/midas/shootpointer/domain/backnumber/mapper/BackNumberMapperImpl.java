package com.midas.shootpointer.domain.backnumber.mapper;

import com.midas.shootpointer.domain.backnumber.dto.BackNumberRequest;
import com.midas.shootpointer.domain.backnumber.dto.BackNumberResponse;
import com.midas.shootpointer.domain.backnumber.entity.BackNumber;
import com.midas.shootpointer.domain.backnumber.entity.BackNumberEntity;
import org.springframework.stereotype.Component;

@Component
public class BackNumberMapperImpl implements BackNumberMapper{
    @Override
    public BackNumberEntity dtoToEntity(BackNumberRequest response) {
        return BackNumberEntity.builder()
                .backNumber(BackNumber.of(response.getBackNumber()))
                .build();
    }

    @Override
    public BackNumberResponse entityToDto(BackNumberEntity entity) {
        return BackNumberResponse.of(entity.getBackNumber().getNumber());
    }
}
