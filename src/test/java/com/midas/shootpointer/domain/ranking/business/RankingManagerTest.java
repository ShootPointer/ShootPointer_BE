package com.midas.shootpointer.domain.ranking.business;

import com.midas.shootpointer.domain.ranking.dto.RankingResponse;
import com.midas.shootpointer.domain.ranking.dto.RankingType;
import com.midas.shootpointer.domain.ranking.entity.RankingDocument;
import com.midas.shootpointer.domain.ranking.helper.RankingUtil;
import com.midas.shootpointer.domain.ranking.mapper.RankingMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Collections;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RankingManagerTest {
    @Mock
    private RankingMapper mapper;

    @Mock
    private RankingUtil rankingUtil;

    @InjectMocks
    private RankingManager manager;

    @Test
    @DisplayName("RankingDocument 조회 시 null 이면 Top10를 직접 조회합니다.")
    void fetchLastData_FETCH_DB() throws IOException {
        //given
        String periodKey="periodKey";
        RankingType type=RankingType.DAILY;
        LocalDateTime now=LocalDateTime.now();

        //when
        when(rankingUtil.getRankingTypeKey(type,now)).thenReturn(periodKey);
        when(rankingUtil.fetchRankingDocumentByPeriodKey(periodKey)).thenReturn(null);

        manager.fetchLastData(now,type);

        //then
        verify(rankingUtil,times(1)).getBeginTime(now,type);
        verify(rankingUtil,times(1)).fetchRankingResult(any(),any());
        verify(mapper,times(1)).resultToResponse(anyList(),eq(type));
        verify(mapper,never()).docToResponse(any());
    }

    @Test
    @DisplayName("RankingDocument가 존재하면 mapper.docToResponse()로 변환합니다.")
    void fetchLastData() throws IOException {
        //given
        RankingType type=RankingType.WEEKLY;
        LocalDateTime now=LocalDateTime.now();
        String periodKey="WEEKLY_2025-W40";
        RankingResponse mockResponse=RankingResponse.builder()
                .rankingList(Collections.emptyList())
                .rankingType(type)
                .build();
        RankingDocument document=RankingDocument.of(Collections.emptyList(),now,type);

        when(rankingUtil.getRankingTypeKey(eq(type),any(LocalDateTime.class))).thenReturn(periodKey);
        when(rankingUtil.fetchRankingDocumentByPeriodKey(periodKey)).thenReturn(document);
        when(mapper.docToResponse(document)).thenReturn(mockResponse);

        //when
        RankingResponse response=manager.fetchLastData(now,type);

        //then
        assertThat(response).isEqualTo(mockResponse);
        verify(rankingUtil).getRankingTypeKey(type,now);
        verify(rankingUtil).fetchRankingDocumentByPeriodKey(periodKey);
        verify(mapper).docToResponse(document);

        verify(rankingUtil,never()).getBeginTime(now,type);
        verify(rankingUtil,never()).fetchRankingResult(any(),any());
        verify(mapper,never()).resultToResponse(anyList(),eq(type));
    }
}