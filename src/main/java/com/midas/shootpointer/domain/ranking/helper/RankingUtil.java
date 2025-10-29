package com.midas.shootpointer.domain.ranking.helper;

import com.midas.shootpointer.domain.ranking.dto.RankingResult;
import com.midas.shootpointer.domain.ranking.dto.RankingType;
import com.midas.shootpointer.domain.ranking.entity.RankingDocument;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;

public interface RankingUtil {
    RankingDocument fetchRankingDocumentByPeriodKey(String periodKey);
    /**
 * Compute a key string that identifies the ranking type for the given timestamp.
 *
 * @param type the ranking type to derive the key for
 * @param now  the reference timestamp used to determine any time-based component of the key
 * @return the ranking type key string that uniquely identifies the ranking period or bucket for the given timestamp
 */
String getRankingTypeKey(RankingType type,LocalDateTime now);
    /**
 * Retrieve ranking results for the specified time window.
 *
 * @param start the start timestamp of the window (inclusive)
 * @param end   the end timestamp of the window (inclusive)
 * @return      a list of RankingResult records that fall within the given time window
 * @throws IOException if an I/O error occurs while fetching ranking data
 */
List<RankingResult> fetchRankingResult(LocalDateTime start,LocalDateTime end) throws IOException;
    /**
 * Compute the start of the ranking window that corresponds to the given end time and ranking type.
 *
 * @param end  the end time of the ranking window
 * @param type the ranking type that determines the window length or boundary rules
 * @return the start time of the ranking window corresponding to `end` and `type` (typically <= `end`)
 */
LocalDateTime getBeginTime(LocalDateTime end,RankingType type);
    /**
 * Computes a composite ranking weight from two-point, three-point, and total score components.
 *
 * @param twoScore   value contributed by two-point scoring
 * @param threeScore value contributed by three-point scoring
 * @param totalScore overall total score value
 * @return           the computed ranking weight; larger values indicate stronger performance
 */
double calculateRankingWeight(int twoScore,int threeScore,int totalScore);
}