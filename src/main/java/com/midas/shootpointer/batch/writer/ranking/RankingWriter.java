package com.midas.shootpointer.batch.writer.ranking;

import com.midas.shootpointer.batch.processor.ranking.RankingProcessor;
import com.midas.shootpointer.domain.ranking.entity.RankingDocument;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.item.Chunk;
import org.springframework.batch.item.ItemWriter;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class RankingWriter implements ItemWriter<RankingDocument> {

    private final MongoTemplate mongoTemplate;
    private final RankingProcessor processor;

    @Override
    public void write(Chunk<? extends RankingDocument> chunk) throws Exception {
        RankingDocument doc=processor.buildDocument();
        mongoTemplate.save(doc);
    }
}
