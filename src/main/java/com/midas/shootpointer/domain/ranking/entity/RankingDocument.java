package com.midas.shootpointer.domain.ranking.entity;

import com.midas.shootpointer.domain.ranking.dto.RankingType;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.List;

@Document(collection="ranking")
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RankingDocument {
    @Id
    private String id;

    private RankingType type;

    //집계 기준 날짜 - 시작
    private LocalDateTime periodBegin;

    //집계 기준 날짜 - 끝
    private LocalDateTime periodEnd;

    private List<RankingEntry> top10;

    private LocalDateTime createdAt;
}
