package com.midas.shootpointer.domain.ranking.entity;

import com.midas.shootpointer.domain.ranking.dto.RankingType;
import jakarta.persistence.Column;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.time.temporal.WeekFields;
import java.util.List;

@Document(collection="ranking")
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RankingDocument {
    @Id
    @Column(nullable = false,unique = true)
    private String id;

    @Indexed
    private RankingType type;

    @Indexed
    //집계 기준 날짜 - 시작
    private LocalDateTime periodBegin;


    private List<RankingEntry> top10;

    @Indexed(name = "type_period_idx",background = true)
    //집계 구분자 - e.g "WEEKLY_2025-10-W1" (10월 1주차)
    private String typePeriodKey;

    /**
     * Builder pattern
     */
    public static RankingDocument of(List<RankingEntry> top10,LocalDateTime periodBegin,RankingType type){
        return RankingDocument.builder()
                .periodBegin(periodBegin)
                .top10(top10)
                .type(type)
                .typePeriodKey(generatePeriodKey(type,periodBegin))
                .build();
    }

    /**
     * 집계 구분자 생성
     */
    private static String generatePeriodKey(RankingType type,LocalDateTime begin){
        switch (type){
            case DAILY -> {
                return String.format("DAILY_%s",begin.toLocalDate());
            }
            case WEEKLY -> {
                return String.format("WEEKLY_%d-W%d",begin.getYear(),begin.get(WeekFields.ISO.weekOfYear()));
            }
            case MONTHLY -> {
                return String.format("MONTHLY_%d-%02d",begin.getYear(),begin.getMonthValue());
            }
        }
        return String.format("DAY_%s",begin.toLocalDate());
    }
}
