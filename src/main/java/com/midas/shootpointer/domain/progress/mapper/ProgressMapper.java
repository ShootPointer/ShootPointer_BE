package com.midas.shootpointer.domain.progress.mapper;

import com.midas.shootpointer.domain.progress.dto.ProgressData;
import com.midas.shootpointer.domain.progress.dto.ProgressResponse;

public interface ProgressMapper {
    ProgressResponse progressDataToResponse(ProgressData data);
}
