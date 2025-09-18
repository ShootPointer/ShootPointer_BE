package com.midas.shootpointer.domain.backnumber.helper;

import com.midas.shootpointer.domain.backnumber.entity.BackNumber;
import com.midas.shootpointer.domain.backnumber.entity.BackNumberEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class BackNumberHelperImpl implements BackNumberHelper{
    private final BackNumberUtil backNumberUtil;

    @Override
    public BackNumberEntity findOrElseGetBackNumber(BackNumber backNumber) {
        return backNumberUtil.findOrElseGetBackNumber(backNumber);
    }
}
