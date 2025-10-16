package com.midas.shootpointer.batch.processor.ranking;

import com.midas.shootpointer.batch.dto.HighlightWithMemberDto;
import com.midas.shootpointer.domain.highlight.entity.HighlightEntity;
import com.midas.shootpointer.domain.ranking.entity.RankingDocument;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.stereotype.Component;

@Component
public class RankingProcessor implements ItemProcessor<HighlightWithMemberDto, RankingDocument> {


    @Override
    public RankingDocument process(HighlightWithMemberDto item) {
        if (item==null) return null;

        /**
         * 1. 비즈니스 규칙 적용
         */
        if (!item.getAgreeToAggregation()) return null;
        if (!item.getIsSelected()) return null;


        /**
         * 2. Highlight Entity 생성
         */
        HighlightEntity highlight=HighlightEntity.builder()
                .highlightId(item.getHighlightId())
                .highlightKey(item.getHighlightKey())
                .isSelected(item.getIsSelected())
                .threePointCount(item.getThreePointCount())
                .twoPointCount(item.getTwoPointCount())
                .highlightURL(item.getHighlightUrl())
                .build();

        /**
         * 3. 점수 계산
         */
        int twoPointTotal=highlight.totalTwoPoint();
        int threePointTotal=highlight.totalThreePoint();
        int totalPoint=twoPointTotal+threePointTotal;

        /**
         * 4.
         */
        return null;
    }
}
