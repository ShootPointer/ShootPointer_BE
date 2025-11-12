package com.midas.shootpointer.domain.progress.mapper;

import com.midas.shootpointer.domain.progress.dto.ProgressData;
import com.midas.shootpointer.domain.progress.dto.ProgressResponse;
import org.springframework.stereotype.Component;

@Component
public class ProgressMapperImpl implements ProgressMapper{
    @Override
    public ProgressResponse progressDataToResponse(ProgressData data) {
        return new ProgressResponse(200,true,data.progress(),data.type());
    }
}
