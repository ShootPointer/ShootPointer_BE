package com.midas.shootpointer.domain.ranking.mapper;

import com.midas.shootpointer.batch.dto.HighlightWithMemberDto;
import com.midas.shootpointer.domain.ranking.dto.RankingResponse;
import com.midas.shootpointer.domain.ranking.entity.RankingDocument;
import com.midas.shootpointer.domain.ranking.entity.RankingEntry;
import org.springframework.stereotype.Component;

@Component
public class RankingDocumentImpl implements RankingDocumentMapper{

    @Override
    public RankingEntry dtoToEntity(HighlightWithMemberDto dto) {
        return RankingEntry.builder()
                .memberId(dto.getMemberId())
                .memberName(dto.getMemberName())
                .threeScore(dto.getThreePointTotal())
                .twoScore(dto.getTwoPointTotal())
                .totalScore(dto.getTotalScore())
                .build();
    }

    @Override
    public RankingResponse docToResponse(RankingDocument document) {
        return RankingResponse.builder()
                .rankingList(document.getTop10())
                .rankingType(document.getType())
                .build();
    }
}
