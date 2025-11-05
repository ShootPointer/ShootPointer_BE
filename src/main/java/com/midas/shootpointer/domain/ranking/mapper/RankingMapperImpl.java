package com.midas.shootpointer.domain.ranking.mapper;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.midas.shootpointer.batch.dto.HighlightWithMemberDto;
import com.midas.shootpointer.domain.ranking.dto.RankingResponse;
import com.midas.shootpointer.domain.ranking.dto.RankingResult;
import com.midas.shootpointer.domain.ranking.dto.RankingType;
import com.midas.shootpointer.domain.ranking.entity.RankingDocument;
import com.midas.shootpointer.domain.ranking.entity.RankingEntry;
import com.midas.shootpointer.global.common.ErrorCode;
import com.midas.shootpointer.global.exception.CustomException;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Component
public class RankingMapperImpl implements RankingMapper{
    private final ObjectMapper objectMapper=new ObjectMapper().findAndRegisterModules();

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
                            .threeScore(result.threeScore())
                            .totalScore(result.totalScore())
                            .twoScore(result.twoScore())
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

    @Override
    public RankingResult convertToRankingResult(Object o) {
        if (o instanceof RankingResult result) return result;
        else if (o instanceof Map<?, ?> map) {
            return objectMapper.convertValue(map,RankingResult.class);
        }

        throw new CustomException(ErrorCode.NOT_CONVERT_TO_RANKING_RESULT);
    }

    @Override
    public RankingEntry convertToRankingEntry(Object o) {
        if (o instanceof RankingEntry entry) return entry;
        else if (o instanceof Map<?,?> map){
            return objectMapper.convertValue(map,RankingEntry.class);
        }
        throw new CustomException(ErrorCode.NOT_CONVERT_TO_RANKING_ENTRY);
    }
}
