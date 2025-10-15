package com.midas.shootpointer.batch.processor.ranking;

import com.midas.shootpointer.domain.highlight.entity.HighlightEntity;
import org.springframework.batch.item.*;
import org.springframework.stereotype.Component;

@Component
public class RankingProcessor implements ItemProcessor<HighlightEntity,HighlightEntity> {


    @Override
    public HighlightEntity process(HighlightEntity item) throws Exception {
        return null;
    }
}
