package com.midas.shootpointer.domain.ranking.business;

import com.midas.shootpointer.domain.ranking.dto.RankingResponse;
import com.midas.shootpointer.domain.ranking.dto.RankingType;
import com.midas.shootpointer.domain.ranking.entity.RankingDocument;
import com.midas.shootpointer.domain.ranking.entity.RankingEntry;
import com.midas.shootpointer.domain.ranking.helper.RankingUtil;
import com.midas.shootpointer.domain.ranking.mapper.RankingMapper;
import com.midas.shootpointer.domain.ranking.repository.RankingRedisRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RankingManagerTest {
    @Mock
    private RankingMapper mapper;

    @Mock
    private RankingUtil rankingUtil;

    @Mock
    private RankingRedisRepository rankingRedisRepository;

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

    @Test
    @DisplayName("redis 조회 결과 List<RankingEntry>가 null 이면 빈 리스트 형태를 변환합니다.")
    void fetchThisData_NULL(){
        //given
        RankingType type=RankingType.MONTHLY;
        RankingResponse expectedResponse=RankingResponse.of(type,Collections.emptyList());

        //when
        when(rankingRedisRepository.getHighlightsWeeklyRanking(type)).thenReturn(null);
        when(mapper.entryToResponse(anyList(),eq(type))).thenReturn(expectedResponse);

        RankingResponse response=manager.fetchThisData(type);

        //then
        verify(rankingRedisRepository).getHighlightsWeeklyRanking(type);
        verify(mapper).entryToResponse(anyList(),eq(type));

        assertThat(response.getRankingList()).isEqualTo(Collections.EMPTY_LIST);
        assertThat(response.getRankingType()).isEqualTo(type);
    }

    @Test
    @DisplayName("redis 조회 결과 List<RankingEntry>가 empty 이면 빈 리스트 형태를 변환합니다.")
    void fetchThisData_IS_EMPTY(){
        //given
        RankingType type=RankingType.MONTHLY;
        RankingResponse expectedResponse=RankingResponse.of(type,Collections.emptyList());

        //when
        when(rankingRedisRepository.getHighlightsWeeklyRanking(type)).thenReturn(Collections.emptyList());
        when(mapper.entryToResponse(anyList(),eq(type))).thenReturn(expectedResponse);

        RankingResponse response=manager.fetchThisData(type);

        //then
        verify(rankingRedisRepository).getHighlightsWeeklyRanking(type);
        verify(mapper).entryToResponse(anyList(),eq(type));

        assertThat(response.getRankingList()).isEqualTo(Collections.EMPTY_LIST);
        assertThat(response.getRankingType()).isEqualTo(type);
    }


    @Test
    @DisplayName("redis 조회 결과 List<RankingEntry>가 존재하면 mapper.entryToResponse로 변환합니다.")
    void fetchThisData(){
        //given
        RankingType type=RankingType.WEEKLY;
        List<RankingEntry> entries=List.of(
                makeRankingEntry(20,10,1),
                makeRankingEntry(10,10,2)
        );
        RankingResponse expectedResponse=RankingResponse.of(type,entries);

        //when
        when(rankingRedisRepository.getHighlightsWeeklyRanking(type)).thenReturn(entries);
        when(mapper.entryToResponse(entries,type)).thenReturn(expectedResponse);

        RankingResponse response=manager.fetchThisData(type);

        //then
        verify(rankingRedisRepository).getHighlightsWeeklyRanking(type);
        verify(mapper).entryToResponse(anyList(),eq(type));

        assertThat(response).isNotNull();
        assertThat(response.getRankingType()).isEqualTo(type);
        assertThat(response.getRankingList()).hasSize(2);
        assertThat(response.getRankingList().get(0).getTotalScore()).isEqualTo(30);
        assertThat(response.getRankingList().get(0).getTwoScore()).isEqualTo(20);
        assertThat(response.getRankingList().get(0).getThreeScore()).isEqualTo(10);
        assertThat(response.getRankingList().get(0).getRank()).isEqualTo(1);

        assertThat(response.getRankingList().get(1).getTotalScore()).isEqualTo(20);
        assertThat(response.getRankingList().get(1).getTwoScore()).isEqualTo(10);
        assertThat(response.getRankingList().get(1).getThreeScore()).isEqualTo(10);
        assertThat(response.getRankingList().get(1).getRank()).isEqualTo(2);


    }

    private RankingEntry makeRankingEntry(int two,int three,int rank){
        return RankingEntry.builder()
                .totalScore(two+three)
                .twoScore(two)
                .threeScore(three)
                .rank(rank)
                .memberName("test"+rank)
                .memberId(UUID.randomUUID())
                .build();
    }
}