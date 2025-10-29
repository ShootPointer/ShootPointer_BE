package com.midas.shootpointer.domain.ranking.business.service;

import com.midas.shootpointer.domain.ranking.dto.RankingResponse;
import com.midas.shootpointer.domain.ranking.dto.RankingType;

import java.io.IOException;
import java.time.LocalDateTime;

public interface RankingService {
    /**
 * Fetches the most recent ranking data up to the specified time for the given ranking type.
 *
 * @param type the ranking category to retrieve
 * @param time the reference time; returns data at or before this timestamp
 * @return a RankingResponse containing the requested ranking data
 * @throws IOException if an I/O error occurs while retrieving the data
 */
RankingResponse fetchLastData(RankingType type, LocalDateTime time) throws IOException;
    /**
 * Retrieve current ranking data for the specified ranking category.
 *
 * @param type the ranking category to fetch data for
 * @return a {@link RankingResponse} containing the current ranking data for the given category
 */
RankingResponse fetchThisData(RankingType type);
}