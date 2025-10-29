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

    /**
     * Create a RankingEntry from a HighlightWithMemberDto.
     *
     * @param dto the DTO containing member and score data
     * @return a RankingEntry populated with memberId, memberName, threeScore, twoScore, and totalScore from the dto
     */
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

    /**
     * Builds a RankingResponse from a list of RankingResult objects, assigning sequential ranks starting at 1.
     *
     * @param results the ordered list of ranking results to convert into entries; each result becomes a RankingEntry with a rank based on its position
     * @param type    the RankingType to set on the resulting response
     * @return        a RankingResponse whose rankingList contains entries converted from {@code results} with ranks assigned starting at 1 and whose rankingType is {@code type}
     */
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

    /**
     * Create a RankingResponse containing the provided entries and ranking type.
     *
     * @param entries list of ranking entries to include in the response
     * @param type the ranking type for the response
     * @return the constructed RankingResponse with the specified type and entries
     */
    @Override
    public RankingResponse entryToResponse(List<RankingEntry> entries, RankingType type) {
        return RankingResponse.of(type,entries);
    }

    /**
     * Convert an arbitrary object into a RankingResult.
     *
     * If the provided object is already a RankingResult it is returned as-is.
     * If the object is a Map, its entries are converted into a RankingResult.
     *
     * @param o the source object to convert; may be a RankingResult or a Map of fields
     * @return the resulting RankingResult
     * @throws CustomException if the object cannot be converted to a RankingResult (ErrorCode.NOT_CONVERT_TO_RANKING_RESULT)
     */
    @Override
    public RankingResult convertToRankingResult(Object o) {
        if (o instanceof RankingResult result) return result;
        else if (o instanceof Map<?, ?> map) {
            return objectMapper.convertValue(map,RankingResult.class);
        }

        throw new CustomException(ErrorCode.NOT_CONVERT_TO_RANKING_RESULT);
    }

    /**
     * Convert the given object to a RankingEntry.
     *
     * If the input is already a RankingEntry it is returned unchanged; if the input is a Map it is converted into a RankingEntry; otherwise a CustomException is thrown.
     *
     * @param o the source object to convert (a RankingEntry or a Map of RankingEntry properties)
     * @return the converted or original RankingEntry
     * @throws CustomException when the input cannot be converted to a RankingEntry (ErrorCode.NOT_CONVERT_TO_RANKING_ENTRY)
     */
    @Override
    public RankingEntry convertToRankingEntry(Object o) {
        if (o instanceof RankingEntry entry) return entry;
        else if (o instanceof Map<?,?> map){
            return objectMapper.convertValue(map,RankingEntry.class);
        }
        throw new CustomException(ErrorCode.NOT_CONVERT_TO_RANKING_ENTRY);
    }
}