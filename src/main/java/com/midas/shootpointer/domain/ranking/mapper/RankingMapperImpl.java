package com.midas.shootpointer.domain.ranking.mapper;

import com.midas.shootpointer.batch.dto.HighlightWithMemberDto;
import com.midas.shootpointer.domain.ranking.dto.RankingResponse;
import com.midas.shootpointer.domain.ranking.dto.RankingResult;
import com.midas.shootpointer.domain.ranking.dto.RankingType;
import com.midas.shootpointer.domain.ranking.entity.RankingDocument;
import com.midas.shootpointer.domain.ranking.entity.RankingEntry;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class RankingMapperImpl implements RankingMapper{

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

    @Override
    public RankingResponse resultToResponse(List<RankingResult> results, RankingType type) {
        List<RankingEntry> entries=new ArrayList<>();
        int rank=1;
        for (RankingResult result:results){
            entries.add(
                    RankingEntry.builder()
                            .memberName(result.memberName())
                            .memberId(result.memberId())
                            .threeScore(result.threeTotal())
                            .totalScore(result.total())
                            .twoScore(result.twoTotal())
                            .rank(rank)
                            .build()
            );
            rank++;
        }

        return RankingResponse.builder()
                .rankingType(type)
                .rankingList(entries)
                .build();
    }

    @Override
    public RankingResponse entryToResponse(List<RankingEntry> entries, RankingType type) {
        return RankingResponse.of(type,entries);
    }
}
