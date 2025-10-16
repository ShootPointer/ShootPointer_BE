package com.midas.shootpointer.batch.reader.ranking;

import com.midas.shootpointer.batch.dto.HighlightWithMemberDto;
import com.midas.shootpointer.domain.ranking.dto.RankingType;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.item.database.JdbcPagingItemReader;
import org.springframework.batch.item.database.Order;
import org.springframework.batch.item.database.PagingQueryProvider;
import org.springframework.batch.item.database.support.PostgresPagingQueryProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;
import java.time.LocalDateTime;
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
    public JdbcPagingItemReader<HighlightWithMemberDto> highlightReader(
            @Value("#{jobParameters['rankingType']}")RankingType rankingType
            ){
        /**
         * 기간 계산 - weekly / monthly
         */
        LocalDateTime end=LocalDateTime.now(); //Job 실행 시점 기준
        LocalDateTime begin=LocalDateTime.now();

        if (rankingType.equals(RankingType.WEEKLY)){
            begin=end.minusDays(7);
        }else if (rankingType.equals(RankingType.MONTHLY)){
            begin=end.minusMonths(1);
        }

        JdbcPagingItemReader<HighlightWithMemberDto> rankingReader=new JdbcPagingItemReader<>();

        rankingReader.setName("rankingReader");
        rankingReader.setDataSource(dataSource);
        rankingReader.setFetchSize(FETCH_SIZE);
        rankingReader.setQueryProvider(pagingQueryProvider(dataSource,begin,end));

        //highlight_with_member mapping
        rankingReader.setRowMapper(((rs, rowNum) -> HighlightWithMemberDto.builder()
                .highlightKey((UUID) rs.getObject("highlight_key"))
                .highlightUrl(rs.getString("highlight_url"))
                .highlightId((UUID) rs.getObject("highlight_id"))
                .memberId((UUID) rs.getObject("member_id"))
                .memberName(rs.getString("member_name"))
                .threePointTotal(rs.getInt("three_point_total"))
                .twoPointTotal(rs.getInt("two_point_total"))
                .totalScore(rs.getInt("total_score"))
                .build()
        ));

        return rankingReader;
    }

    /**
     * 커스텀 PagingQueryProvider
     */
    private PagingQueryProvider pagingQueryProvider(DataSource dataSource, LocalDateTime begin,LocalDateTime end){
        PostgresPagingQueryProvider queryProvider=new PostgresPagingQueryProvider();
        //Highlight Member 데이터 조회
        queryProvider.setSelectClause("""
            SELECT
                h.highlight_id, h.highlight_url, h.highlight_key,
                m.is_aggregation_agreed, m.member_id, m.member_name,
                SUM(h.two_point_count) as two_point_total,
                SUM(h.three_point_count) as three_point_total,
                (SUM(h.two_point_count) * 2 + SUM(h.three_point_count)) as total_score
            """);

        queryProvider.setFromClause("""
            FROM highlight h
            JOIN member m ON h.member_id = m.member_id
            """);

        queryProvider.setWhereClause("""
                WHERE m.is_aggregation_agreed = true AND h.is_selected = true
                    AND h.created_at BETWEEN :begin AND :end
                """);

        queryProvider.setGroupClause("""
                GROUP BY m.member_id
                """);

        /*
        * 내림차순 정렬
        * 1. 총합 점수
        * 2. 3점 총합
        * 3. 2점 총합
         */
        queryProvider.setSortKeys(Map.of(
                "total_score",Order.DESCENDING,
                "three_point_total",Order.DESCENDING,
                "two_point_total",Order.DESCENDING
        ));

        //Paging 정렬 - highlight_id 기준 오름차순
        queryProvider.setSortKeys(Map.of("h.highlight_id", Order.ASCENDING));

        return queryProvider;
    }
}
