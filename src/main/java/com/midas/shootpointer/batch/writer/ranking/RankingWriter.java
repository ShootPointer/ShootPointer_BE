package com.midas.shootpointer.batch.writer.ranking;

import com.midas.shootpointer.domain.highlight.entity.HighlightEntity;
import org.springframework.batch.item.Chunk;
import org.springframework.batch.item.ItemWriter;
import org.springframework.stereotype.Component;

@Component
public class RankingWriter implements ItemWriter<HighlightEntity> {
    @Override
    public void write(Chunk<? extends HighlightEntity> chunk) throws Exception {

    }
}
