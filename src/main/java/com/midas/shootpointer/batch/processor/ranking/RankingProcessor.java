package com.midas.shootpointer.batch.processor.ranking;

import com.midas.shootpointer.batch.dto.HighlightWithMemberDto;
import com.midas.shootpointer.domain.ranking.dto.RankingType;
import com.midas.shootpointer.domain.ranking.entity.RankingDocument;
import com.midas.shootpointer.domain.ranking.entity.RankingEntry;
import com.midas.shootpointer.domain.ranking.mapper.RankingDocumentMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.*;

@Component
@RequiredArgsConstructor
public class RankingProcessor implements ItemProcessor<List<HighlightWithMemberDto>, RankingDocument> {
    private final RankingDocumentMapper mapper;

    @Value("#{jobParameters['end']}")
    private LocalDateTime end;

    @Value("#{jobParameters['rankingType']}")
    private RankingType type;

    @Override
    public RankingDocument process(List<HighlightWithMemberDto> item) {

        /**
         * 1. RankingEntry 생성
         */
        List<RankingEntry> entries = item.stream()
                .map(mapper::dtoToEntity)
                .toList();

        /**
         * 2. RankingDocument mapping
         */
        return RankingDocument.of(entries,end,type);
    }
}
