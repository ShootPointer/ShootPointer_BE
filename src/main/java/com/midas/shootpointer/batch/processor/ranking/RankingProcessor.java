package com.midas.shootpointer.batch.processor.ranking;

import com.midas.shootpointer.batch.dto.HighlightWithMemberDto;
import com.midas.shootpointer.domain.ranking.entity.RankingDocument;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.stereotype.Component;

@Component
public class RankingProcessor implements ItemProcessor<HighlightWithMemberDto, RankingDocument> {


    @Override
    public RankingDocument process(HighlightWithMemberDto item) throws Exception {
        return null;
    }
}
