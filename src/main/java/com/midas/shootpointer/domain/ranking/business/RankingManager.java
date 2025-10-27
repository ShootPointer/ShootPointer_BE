package com.midas.shootpointer.domain.ranking.business;

import com.midas.shootpointer.domain.ranking.dto.RankingResponse;
import com.midas.shootpointer.domain.ranking.dto.RankingType;
import com.midas.shootpointer.domain.ranking.entity.RankingDocument;
import com.midas.shootpointer.domain.ranking.entity.RankingEntry;
import com.midas.shootpointer.domain.ranking.helper.RankingUtil;
import com.midas.shootpointer.domain.ranking.mapper.RankingMapper;
import com.midas.shootpointer.domain.ranking.repository.RankingRedisRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

@Component
@RequiredArgsConstructor
public class RankingManager {
    private final RankingMapper mapper;
    private final RankingUtil rankingUtil;
    private final RankingRedisRepository redisRepository;

    /**
     * 전 날 랭킹 집계 조회
     * @param time 집계 날짜
     * @param type 집계 유형 - Daily(1일 단위) / Weekly(일주일 단위) / Monthly(1달 단위)
     * @return RankingResponse
     */
    public RankingResponse fetchLastData(LocalDateTime time, RankingType type) throws IOException {
        /**
         * 1. period Key 생성
         */
        String periodKey=rankingUtil.getRankingTypeKey(type,time);

        /**
         * 2. RankingDocument 조회
         */
        RankingDocument document=rankingUtil.fetchRankingDocumentByPeriodKey(periodKey);

        /**
         * 3. 결과값이 null인 경우 - Top 10 직접 조회
         */
        if (document==null){
            LocalDateTime endTime=rankingUtil.getBeginTime(time,type);
           return mapper.resultToResponse(rankingUtil.fetchRankingResult(endTime,time),type);
        }

        return mapper.docToResponse(document);
    }


    public RankingResponse fetchThisData(RankingType type){
        List<RankingEntry> results=redisRepository.getHighlightsWeeklyRanking(type);

        /**
         * 조회값이 null인 경우
         */
        if (results==null || results.isEmpty()) {
            return mapper.entryToResponse(Collections.emptyList(),type);
        }

        return mapper.entryToResponse(results,type);
    }

}
