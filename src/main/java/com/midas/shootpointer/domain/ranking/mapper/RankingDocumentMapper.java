package com.midas.shootpointer.domain.ranking.mapper;

import com.midas.shootpointer.domain.highlight.entity.HighlightEntity;
import com.midas.shootpointer.domain.ranking.entity.RankingDocument;

import java.time.LocalDateTime;

public interface RankingDocumentMapper {
    RankingDocument entityToDoc(HighlightEntity entity, LocalDateTime createdAt);
}
