package com.midas.shootpointer.domain.ranking.business.service;

import com.midas.shootpointer.domain.ranking.business.RankingManager;
import com.midas.shootpointer.domain.ranking.dto.RankingResponse;
import com.midas.shootpointer.domain.ranking.dto.RankingType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class RankingServiceImpl implements RankingService{
    private final RankingManager rankingManager;
    @Override
    public RankingResponse fetchLastData(RankingType type, LocalDateTime time) {
        return rankingManager.fetchLastData(time,type);
    }
}
