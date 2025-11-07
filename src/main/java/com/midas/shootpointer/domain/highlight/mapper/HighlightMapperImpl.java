package com.midas.shootpointer.domain.highlight.mapper;

import com.midas.shootpointer.domain.highlight.dto.HighlightSelectResponse;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

@Component
public class HighlightMapperImpl implements HighlightMapper{

    @Override
    public HighlightSelectResponse entityToResponse(List<UUID> selectedHighlights) {
        return HighlightSelectResponse.builder()
                .selectedHighlightIds(selectedHighlights)
                .build();
    }

}
