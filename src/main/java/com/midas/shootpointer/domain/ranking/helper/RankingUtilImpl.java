package com.midas.shootpointer.domain.ranking.helper;

import com.midas.shootpointer.domain.ranking.dto.RankingResult;
import com.midas.shootpointer.domain.ranking.dto.RankingType;
import com.midas.shootpointer.domain.ranking.entity.RankingDocument;
import com.midas.shootpointer.domain.ranking.repository.RankingRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.time.temporal.WeekFields;
import java.util.List;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class RankingUtilImpl implements RankingUtil {
    @Value("${query.path.ranking}")
    private String queryPath;

    private final RankingRepository rankingRepository;
    private final JdbcTemplate jdbcTemplate;

    private final int TWO_WEIGHT=1;
    private final int THREE_WEIGHT=1_000;
    private final int TOTAL_WEIGHT=1_000_000;
    /**
     * Period Key로 랭킹 Document 조회
     * @param periodKey 조회 키
     * @return RankingDocument
     */
    @Override
    public RankingDocument fetchRankingDocumentByPeriodKey(String periodKey) {
        return rankingRepository.findByTypePeriodKey(periodKey);
    }

    /**
     * RankingDocument의 type Period Key 생성.
     * @param type 랭킹 집계 유형
     * @param now 조회 날짜
     * @return type Period Key
     */
    @Override
    public String getRankingTypeKey(RankingType type, LocalDateTime now) {
        now=now.withHour(0).withMinute(0).withSecond(0).withNano(0);
        switch (type){
            case MONTHLY -> {
                now=now.withDayOfMonth(1).minusMonths(1);
                return String.format("MONTHLY_%d-%02d",now.getYear(),now.getMonthValue());
            }
            case WEEKLY ->{
                now=now.with(DayOfWeek.MONDAY).minusDays(7);
                return String.format("WEEKLY_%d-W%d",now.getYear(),now.get(WeekFields.ISO.weekOfYear()));
            }
            case DAILY -> {
                now=now.minusDays(1);
                return String.format("DAILY_%s",now.toLocalDate());
            }
        }
        return "";
    }

    /**
     * RankingDoc 정보가 없을 시 DB에서 데이터 조회
     * @param start
     * @param end
     * @return
     */
    @Override
    public List<RankingResult> fetchRankingResult(LocalDateTime start, LocalDateTime end) throws IOException {
        String sql= Files.readString(Paths.get(queryPath));

        return jdbcTemplate.query(sql,
                ps -> {
                    ps.setObject(1,start);
                    ps.setObject(2,end);
                },
                (rs,rowNum)->
                 new RankingResult(
                         rs.getString("member_name"),
                         rs.getObject("member_id", UUID.class),
                         rs.getInt("total"),
                         rs.getInt("two_total"),
                         rs.getInt("three_total")
                 )
        );
    }

    /**
     * Compute the start timestamp for the ranking period that ends at the given time for the specified ranking type.
     *
     * @param end  the end time of the period; calculations normalize this to the start of that day (00:00) before adjusting
     * @param type the ranking period type that determines how far back the start time is set:
     *             MONTHLY -> first day of the previous month at 00:00,
     *             WEEKLY  -> Monday of the previous week at 00:00,
     *             DAILY   -> previous day at 00:00
     * @return the calculated period start LocalDateTime at 00:00 for the given ranking type
     */
    @Override
    public LocalDateTime getBeginTime(LocalDateTime end,RankingType type) {
        LocalDateTime start=end;
        start=start.withHour(0).withMinute(0).withSecond(0).withNano(0);
        switch (type){
            case MONTHLY -> start=start.withDayOfMonth(1).minusMonths(1);
            case WEEKLY -> start=start.with(DayOfWeek.MONDAY).minusDays(7);
            case DAILY -> start=start.minusDays(1);

        }
        return start;
    }

    /**
     * Compute the weighted ranking score from two-point, three-point, and total scores.
     *
     * @param twoScore   sum of two-point scores
     * @param threeScore sum of three-point scores
     * @param totalScore overall total score
     * @return the weighted ranking score computed using the configured weights
     */
    @Override
    public double calculateRankingWeight(int twoScore, int threeScore,int totalScore) {
        return (double) twoScore*TWO_WEIGHT + (double) threeScore*THREE_WEIGHT + (double)totalScore *TOTAL_WEIGHT;
    }
}