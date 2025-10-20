package com.midas.shootpointer.domain.ranking.business;

import com.midas.shootpointer.domain.ranking.dto.RankingResponse;
import com.midas.shootpointer.domain.ranking.dto.RankingType;
import com.midas.shootpointer.domain.ranking.entity.RankingDocument;
import com.midas.shootpointer.domain.ranking.helper.RankingUtil;
import com.midas.shootpointer.domain.ranking.mapper.RankingDocumentMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Collections;

@Component
@RequiredArgsConstructor
public class RankingManager {
    private final RankingDocumentMapper mapper;
    private final RankingUtil rankingUtil;

    /**
     * 전 날 랭킹 집계 조회
     * @param time 집계 날짜
     * @param type 집계 유형 - Daily(1일 단위) / Weekly(일주일 단위) / Monthly(1달 단위)
     * @return RankingResponse
     */
    public RankingResponse fetchLastData(LocalDateTime time, RankingType type){
        /**
         * 1. period Key 생성
         */
        String periodKey=rankingUtil.getRankingTypeKey(type,time);

        /**
         * 2. RankingDocument 조회
         */
        RankingDocument document=rankingUtil.fetchRankingDocumentByPeriodKey(periodKey);

        /**
         * 3. 결과값이 null인 경우 - Top 10 리스트 빈 리스트로
         */
        if (document==null){
            return mapper.docToResponse(
                    RankingDocument.of(Collections.emptyList(),time,type)
            );
        }

        return mapper.docToResponse(document);
    }

}
