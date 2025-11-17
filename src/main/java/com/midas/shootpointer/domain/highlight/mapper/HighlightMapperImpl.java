package com.midas.shootpointer.domain.highlight.mapper;

import com.midas.shootpointer.domain.highlight.dto.HighlightCalendarDaysResponse;
import com.midas.shootpointer.domain.highlight.dto.HighlightInfoResponse;
import com.midas.shootpointer.domain.highlight.dto.HighlightSelectResponse;
import com.midas.shootpointer.domain.highlight.entity.HighlightEntity;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.TreeMap;
import java.util.UUID;

@Component
public class HighlightMapperImpl implements HighlightMapper{

    @Override
    public HighlightSelectResponse entityToResponse(List<UUID> selectedHighlights) {
        return HighlightSelectResponse.builder()
                .selectedHighlightIds(selectedHighlights)
                .build();
    }

    @Override
    public HighlightInfoResponse infoResponseToEntity(HighlightEntity entity) {
        return HighlightInfoResponse.of(entity.getHighlightId(),entity.getCreatedAt(),entity.totalTwoPoint(),entity.totalThreePoint(),entity.getHighlightURL());
    }

    @Override
    public List<HighlightCalendarDaysResponse> groupingHighlightToDaysResponse(TreeMap<LocalDate, List<HighlightInfoResponse>> groupingHighlights) {
        List<HighlightCalendarDaysResponse> calendarDaysResponses=new ArrayList<>();

        for (LocalDate date:groupingHighlights.keySet()){
            List<HighlightInfoResponse> daysResponse=groupingHighlights.get(date);

            calendarDaysResponses.add(new HighlightCalendarDaysResponse(date,daysResponse.size(),daysResponse));
        }
        return calendarDaysResponses;
    }


}
