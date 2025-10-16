package com.midas.shootpointer.domain.ranking.entity;

import com.midas.shootpointer.domain.ranking.dto.RankingType;
import jakarta.persistence.Column;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.List;

@Document(collection="ranking")
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RankingDocument {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(nullable = false,unique = true)
    private String id;

    @Indexed
    private RankingType type;

    @Indexed
    //집계 기준 날짜 - 시작
    private LocalDateTime periodBegin;

    @Indexed
    //집계 기준 날짜 - 끝
    private LocalDateTime periodEnd;

    private List<RankingEntry> top10;

    @Indexed(name = "type_period_idx",background = true)
    //집계 구분자 - e.g "WEEKLY_2025-10-1" (10월 1주차)
    private String typePeriodKey;
}
