package com.midas.shootpointer.domain.ranking.business.service;

import com.midas.shootpointer.domain.ranking.business.RankingManager;
import com.midas.shootpointer.domain.ranking.dto.RankingResponse;
import com.midas.shootpointer.domain.ranking.dto.RankingType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class RankingServiceImpl implements RankingService{
    private final RankingManager rankingManager;
    /**
     * Fetches the most recent ranking data for the specified type at or before the given time.
     *
     * @param type the ranking type to retrieve
     * @param time the reference time to locate the most recent data (at or before this time)
     * @return the RankingResponse containing the most recent data for the specified type relative to the given time
     * @throws IOException if an I/O error occurs while retrieving the data
     */
    @Override
    public RankingResponse fetchLastData(RankingType type, LocalDateTime time) throws IOException {
        return rankingManager.fetchLastData(time,type);
    }

    /**
     * Retrieves the current ranking data for the given ranking type.
     *
     * @param type the ranking type to retrieve data for
     * @return the current RankingResponse for the specified type
     */
    @Override
    public RankingResponse fetchThisData(RankingType type) {
        return rankingManager.fetchThisData(type);
    }
}