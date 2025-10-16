package com.midas.shootpointer.batch.reader.ranking;

import com.midas.shootpointer.batch.dto.HighlightWithMemberDto;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.item.database.JdbcPagingItemReader;
import org.springframework.batch.item.database.Order;
import org.springframework.batch.item.database.PagingQueryProvider;
import org.springframework.batch.item.database.support.PostgresPagingQueryProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;
import java.util.Map;
import java.util.UUID;

@Configuration
public class RankingReader extends JdbcPagingItemReader<HighlightWithMemberDto> {
    /**
     * Page Size
     */
    private final int FETCH_SIZE=1_000;

    @Autowired
    private DataSource dataSource;

    @Bean
    @StepScope
    public JdbcPagingItemReader<HighlightWithMemberDto> highlightReader(){
        JdbcPagingItemReader<HighlightWithMemberDto> rankingReader=new JdbcPagingItemReader<>();

        rankingReader.setName("rankingReader");
        rankingReader.setDataSource(dataSource);
        rankingReader.setFetchSize(FETCH_SIZE);
        rankingReader.setQueryProvider(pagingQueryProvider(dataSource));

        //highlight_with_member mapping
        rankingReader.setRowMapper(((rs, rowNum) -> HighlightWithMemberDto.builder()
                .highlightKey((UUID) rs.getObject("highlight_key"))
                .highlightUrl(rs.getString("highlight_url"))
                .threePointCount(rs.getInt("three_point_count"))
                .twoPointCount(rs.getInt("two_point_count"))
                .isSelected(rs.getBoolean("is_selected"))
                .highlightId((UUID) rs.getObject("highlight_id"))
                .agreeToAggregation(rs.getBoolean("is_aggregation_agreed"))
                .memberId((UUID) rs.getObject("member_id"))
                .memberName(rs.getString("member_name"))
                .build()
        ));

        return rankingReader;
    }

    /**
     * 커스텀 PagingQueryProvider
     */
    private PagingQueryProvider pagingQueryProvider(DataSource dataSource){
        PostgresPagingQueryProvider queryProvider=new PostgresPagingQueryProvider();
        //Highlight Member 데이터 조회
        queryProvider.setSelectClause("""
            SELECT h.highlight_id, h.highlight_url, h.highlight_key, h.is_selected, h.two_point_count, h.three_point_count,
                   m.is_aggregation_agreed, m.member_id, m.member_name
            """);

        queryProvider.setFromClause("""
            FROM highlight h
            JOIN member m ON h.member_id = m.member_id
            """);

        //Paging 정렬 - highlight_id 기준 오름차순
        queryProvider.setSortKeys(Map.of("h.highlight_id", Order.ASCENDING));

        return queryProvider;
    }
}
