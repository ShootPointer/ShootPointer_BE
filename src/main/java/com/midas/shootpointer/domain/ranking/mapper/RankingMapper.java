package com.midas.shootpointer.domain.ranking.mapper;

import com.midas.shootpointer.batch.dto.HighlightWithMemberDto;
import com.midas.shootpointer.domain.ranking.dto.RankingResponse;
import com.midas.shootpointer.domain.ranking.dto.RankingResult;
import com.midas.shootpointer.domain.ranking.dto.RankingType;
import com.midas.shootpointer.domain.ranking.entity.RankingDocument;
import com.midas.shootpointer.domain.ranking.entity.RankingEntry;

import java.util.List;

public interface RankingMapper {
    /**
 * Create a RankingEntry entity from a HighlightWithMemberDto.
 *
 * @param dto DTO containing highlight and member information used to construct the entry
 * @return the RankingEntry populated from the provided DTO
 */
RankingEntry dtoToEntity(HighlightWithMemberDto dto);
    /**
 * Create a RankingResponse from a persisted RankingDocument.
 *
 * @param document the persisted ranking document to convert
 * @return a RankingResponse containing the ranking information from the document
 */
RankingResponse docToResponse(RankingDocument document);
    /**
 * Builds a RankingResponse from a list of ranking results and the specified ranking type.
 *
 * @param result the list of RankingResult objects to include in the response
 * @param type   the RankingType that classifies or labels the response
 * @return       a RankingResponse representing the provided results for the given ranking type
 */
RankingResponse resultToResponse(List<RankingResult> result, RankingType type);
    /**
 * Builds a RankingResponse representing the provided ranking entries for the specified ranking type.
 *
 * @param entries the ranking entries to include in the response
 * @param type    the ranking type that classifies or labels the response
 * @return        a RankingResponse containing the given entries and metadata corresponding to the ranking type
 */
RankingResponse entryToResponse(List<RankingEntry> entries,RankingType type);
    /**
 * Converts an arbitrary object into a RankingResult.
 *
 * @param o an object containing ranking information to be converted
 * @return a RankingResult constructed from the provided object
 */
RankingResult convertToRankingResult(Object o);
    /**
 * Converts a generic source object into a RankingEntry.
 *
 * @param o the source object containing ranking entry data
 * @return the mapped {@code RankingEntry}
 */
RankingEntry convertToRankingEntry(Object o);
}