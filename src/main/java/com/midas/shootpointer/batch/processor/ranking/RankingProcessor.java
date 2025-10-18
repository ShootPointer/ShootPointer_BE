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
import java.util.ArrayList;
import java.util.List;

/**
 * total_score , three_point_total , two_point_total 오름차순 정렬
 * 상위 10위 정제
 */
@Component
@RequiredArgsConstructor
public class RankingProcessor implements ItemProcessor<HighlightWithMemberDto, RankingDocument> {
    private final RankingDocumentMapper mapper;

    private final List<RankingEntry> aggregated=new ArrayList<>();

    @Value("#{jobParameters['end']}")
    private LocalDateTime end;

    @Value("#{jobParameters['rankingType']}")
    private RankingType type;

    @Override
    public RankingDocument process(HighlightWithMemberDto item) throws Exception {
        /**
         * aggregated에 누적만 진행
         */
        aggregated.add(mapper.dtoToEntity(item));
        return null;
    }

    /**
     * 랭킹 집계 및 정렬 수행
     */
    public RankingDocument buildDocument(){

        List<RankingEntry> top10=aggregated.stream()
                .sorted((m1,m2)->{
                    if (m1.getTotalScore().equals(m2.getTotalScore())){
                        //3순위 - 2점 합계 내림차순
                        if (m1.getThreeScore().equals(m2.getThreeScore())){
                            return Integer.compare(m2.getTwoScore(),m1.getTwoScore());
                        }

                        //2순위 - 3점 합계 내림차순
                        return Integer.compare(m2.getThreeScore(),m1.getThreeScore());
                    }
                    return Integer.compare(m2.getTotalScore(),m1.getTotalScore());
                })
                .limit(10)
                .toList();

        return RankingDocument.of(top10,end,type);
    }
}
