package com.midas.shootpointer.domain.backnumber.helper;

import com.midas.shootpointer.domain.backnumber.entity.BackNumber;
import com.midas.shootpointer.domain.backnumber.entity.BackNumberEntity;
import com.midas.shootpointer.domain.backnumber.repository.BackNumberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class BackNumberUtilImpl implements BackNumberUtil {
    private final BackNumberRepository backNumberRepository;

    @Override
    public BackNumberEntity findOrElseGetBackNumber(BackNumber backNumber) {
        return backNumberRepository.findByBackNumber(backNumber)
                .orElseGet(() -> {
                    BackNumberEntity newEntity = BackNumberEntity.builder()
                            .backNumber(backNumber)
                            .build();
                    return backNumberRepository.save(newEntity);
                });
    }
}
