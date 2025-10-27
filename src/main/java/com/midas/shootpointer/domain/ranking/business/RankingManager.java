package com.midas.shootpointer.domain.ranking.business;

import com.midas.shootpointer.domain.ranking.dto.RankingResponse;
import com.midas.shootpointer.domain.ranking.dto.RankingType;
import com.midas.shootpointer.domain.ranking.entity.RankingDocument;
import com.midas.shootpointer.domain.ranking.helper.RankingUtil;
import com.midas.shootpointer.domain.ranking.mapper.RankingMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
public class RankingManager {
    private final RankingMapper mapper;
    private final RankingUtil rankingUtil;

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

    //TODO: Redis-sorted set 이용하여 이번 주/이번 달 랭킹 구현 예정.
    public RankingResponse fetchThisData(RankingType type){
        return RankingResponse.builder().build();
    }

}
