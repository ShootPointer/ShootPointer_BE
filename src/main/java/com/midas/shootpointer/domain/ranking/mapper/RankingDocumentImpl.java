package com.midas.shootpointer.domain.ranking.mapper;

import com.midas.shootpointer.domain.highlight.entity.HighlightEntity;
import com.midas.shootpointer.domain.ranking.entity.RankingDocument;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class RankingDocumentImpl implements RankingDocumentMapper{

    @Override
    public RankingDocument entityToDoc(HighlightEntity entity, LocalDateTime createdAt) {
        return RankingDocument.builder()
                .createdAt(createdAt)

    }
}
