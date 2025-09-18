package com.midas.shootpointer.domain.backnumber.helper;

import com.midas.shootpointer.domain.backnumber.entity.BackNumber;
import com.midas.shootpointer.domain.backnumber.entity.BackNumberEntity;

public interface BackNumberUtil {
    BackNumberEntity findOrElseGetBackNumber(BackNumber backNumber);
}
