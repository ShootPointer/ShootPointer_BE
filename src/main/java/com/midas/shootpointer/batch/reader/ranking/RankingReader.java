package com.midas.shootpointer.batch.reader.ranking;

import com.midas.shootpointer.domain.highlight.entity.HighlightEntity;
import org.springframework.batch.item.database.JdbcPagingItemReader;
import org.springframework.stereotype.Component;

@Component
public class RankingReader extends JdbcPagingItemReader<HighlightEntity> {

}
