package com.midas.shootpointer.domain.ranking.helper;

import com.midas.shootpointer.domain.ranking.dto.RankingType;
import com.midas.shootpointer.domain.ranking.entity.RankingDocument;
import com.midas.shootpointer.domain.ranking.repository.RankingRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.time.temporal.WeekFields;

@Component
@RequiredArgsConstructor
public class RankingUtilImpl implements RankingUtil {
    private final RankingRepository rankingRepository;


    /**
     * Period Key로 랭킹 Document 조회
     * @param periodKey 조회 키
     * @return RankingDocument
     */
    @Override
    public RankingDocument fetchRankingDocumentByPeriodKey(String periodKey) {
        return rankingRepository.findByTypePeriodKey(periodKey);
    }

    /**
     * RankingDocument의 type Period Key 생성.
     * @param type 랭킹 집계 유형
     * @param now 조회 날짜
     * @return type Period Key
     */
    @Override
    public String getRankingTypeKey(RankingType type, LocalDateTime now) {
        now=now.withHour(0).withMinute(0).withSecond(0).withNano(0);
        switch (type){
            case MONTHLY -> {
                now=now.withDayOfMonth(1).minusMonths(1);
                return String.format("MONTHLY_%d-%02d",now.getYear(),now.getMonthValue());
            }
            case WEEKLY ->{
                now=now.with(DayOfWeek.MONDAY).minusDays(7);
                return String.format("WEEKLY_%d-W%d",now.getYear(),now.get(WeekFields.ISO.weekOfYear()));
            }
            case DAILY -> {
                now=now.minusDays(1);
                return String.format("DAILY_%s",now.toLocalDate());
            }
        }
        return "";
    }
}
