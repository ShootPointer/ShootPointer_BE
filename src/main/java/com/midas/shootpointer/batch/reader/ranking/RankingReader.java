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
            @Value("#{jobParameters['rankingType']}")RankingType rankingType,
            @Value("#{jobParameters['end']}")String endStr
            ){
        /**
         * 기간 계산 - weekly / monthly / daily
         */
        LocalDateTime end=LocalDateTime.parse(endStr);
        LocalDateTime begin=end;

        switch (rankingType){
            case DAILY -> begin=end.minusDays(1);
            case WEEKLY -> begin=end.minusDays(7);
            case MONTHLY -> begin=end.minusMonths(1);
        }

        JdbcPagingItemReader<HighlightWithMemberDto> rankingReader=new JdbcPagingItemReader<>();

        rankingReader.setName("rankingReader");
        rankingReader.setDataSource(dataSource);
        rankingReader.setFetchSize(FETCH_SIZE);
        rankingReader.setQueryProvider(pagingQueryProvider());
        rankingReader.setParameterValues(Map.of(
                "begin",begin,
                "end",end
        ));

        //highlight_with_member mapping
        rankingReader.setRowMapper(((rs, rowNum) -> HighlightWithMemberDto.builder()
                .memberId((UUID) rs.getObject("member_id"))
                .memberName(rs.getString("member_name"))
                .threePointTotal(rs.getInt("three_point_total"))
                .twoPointTotal(rs.getInt("two_point_total"))
                .totalScore(rs.getInt("total_score"))
                .rank(rs.getInt("rank"))
                .build()
        ));

        return rankingReader;
    }

    /**
     * 커스텀 PagingQueryProvider
     */
    private PagingQueryProvider pagingQueryProvider(){
        PostgresPagingQueryProvider queryProvider=new PostgresPagingQueryProvider();
        //Highlight Member 데이터 조회

        //1. rank 집계 - with절
        queryProvider.setSelectClause("""
            member_id,
            member_name,
            total_score,
            two_point_total,
            three_point_total,
            DENSE_RANK() OVER (
                ORDER BY total_score DESC, three_point_total DESC, two_point_total DESC
            ) AS rank
        """);

        queryProvider.setFromClause("""
            FROM (
                SELECT
                    m.member_id,
                    m.member_name,
                    SUM(h.two_point_count * 2) AS two_point_total,
                    SUM(h.three_point_count * 3) AS three_point_total,
                    (SUM(h.two_point_count * 2) + SUM(h.three_point_count * 3)) AS total_score
                FROM highlight h
                JOIN member m ON h.member_id = m.member_id
                WHERE m.is_aggregation_agreed = true
                  AND h.is_selected = true
                  AND h.created_at BETWEEN :begin AND :end
                GROUP BY m.member_id, m.member_name
            ) AS member_scores
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

        return queryProvider;
    }
}
