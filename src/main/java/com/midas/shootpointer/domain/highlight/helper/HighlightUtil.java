package com.midas.shootpointer.domain.highlight.helper;

import com.midas.shootpointer.domain.highlight.dto.HighlightInfoResponse;
import com.midas.shootpointer.domain.highlight.dto.PeriodHighlightResponse;
import com.midas.shootpointer.domain.highlight.dto.PeriodType;
import com.midas.shootpointer.domain.highlight.entity.HighlightEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public interface HighlightUtil {
    String getDirectoryPath(String highlightKey);
    HighlightEntity findHighlightByHighlightId(UUID highlightId);
    List<HighlightEntity> savedAll(List<HighlightEntity> entities);
    Page<HighlightEntity> fetchMembersHighlights(UUID memberId, Pageable pageable);
    List<PeriodHighlightResponse> fetchAllMembersHighlights(PeriodType period);
    LocalDateTime calculateStartDate(PeriodType type,LocalDateTime now);
    LocalDateTime calculateEndDate(PeriodType type,LocalDateTime now);
    Map<LocalDate,List<HighlightInfoResponse>> groupingHighlights(List<HighlightInfoResponse> flatHighlightList);
    List<HighlightInfoResponse> fetchFlatHighlightList(int year,int month,UUID memberId);
}
