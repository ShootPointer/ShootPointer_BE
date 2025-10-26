package com.midas.shootpointer.domain.ranking.business.service;

import com.midas.shootpointer.domain.ranking.dto.RankingResponse;
import com.midas.shootpointer.domain.ranking.dto.RankingType;

import java.io.IOException;
import java.time.LocalDateTime;

public interface RankingService {
    RankingResponse fetchLastData(RankingType type, LocalDateTime time) throws IOException;
}
