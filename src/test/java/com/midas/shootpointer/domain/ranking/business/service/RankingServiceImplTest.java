package com.midas.shootpointer.domain.ranking.business.service;

import com.midas.shootpointer.domain.ranking.business.RankingManager;
import com.midas.shootpointer.domain.ranking.dto.RankingResponse;
import com.midas.shootpointer.domain.ranking.dto.RankingType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;
import java.time.LocalDateTime;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RankingServiceImplTest {
    @InjectMocks
    private RankingServiceImpl rankingService;

    @Mock
    private RankingManager rankingManager;

    @Test
    @DisplayName("rankingManger - fetchLastData(RankingType,LocalDateTime)이 실행되는지 검증합니다.")
    void fetchLastData() throws IOException {
        //given
        RankingType type=RankingType.WEEKLY;
        LocalDateTime now=LocalDateTime.now();
        RankingResponse response=RankingResponse.builder().build();

        //when
        when(rankingManager.fetchLastData(now,type)).thenReturn(response);
        rankingService.fetchLastData(type,now);

        //then
        verify(rankingManager).fetchLastData(any(),any());
    }

}