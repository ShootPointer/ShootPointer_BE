package com.midas.shootpointer.domain.ranking.helper;

import com.midas.shootpointer.domain.ranking.dto.RankingResult;
import com.midas.shootpointer.domain.ranking.dto.RankingType;
import com.midas.shootpointer.domain.ranking.entity.RankingDocument;
import com.midas.shootpointer.domain.ranking.entity.RankingEntry;
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

    private final static int TWO_WEIGHT=1;
    private final static int THREE_WEIGHT=1_000;
    private final static int TOTAL_WEIGHT=1_000_000;
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
     * Redis 가중치 계산
     * @param twoScore 2점슛 합
     * @param threeScore 3점슛 합
     * @return 전체 가중치
     */
    @Override
    public double calculateRankingWeight(int twoScore, int threeScore,int totalScore) {
        return (double) twoScore*TWO_WEIGHT + (double) threeScore*THREE_WEIGHT + (double)totalScore *TOTAL_WEIGHT;
    }

    /**
     * Query 에서 조회한 RankingEntry의 rank값 삽입 메서드
     * @param origin DB 에서 조회한 RankingEntry 값
     * @return rank 값이 삽입된 리스트
     */
    @Override
    public List<RankingEntry> calculateRanking(List<RankingEntry> origin) {
        int rank=1;

        for (RankingEntry entry:origin){
            entry.setRank(rank++);
        }
        return origin;
    }

    /**
     * type 값에 따라 이번주/이번달 시작일 반환
     * @param now : 현재 날짜
     * @param type : 주 / 월 / 일
     * @return 이번주 / 이번달 시작 날짜
     */
    @Override
    public LocalDateTime calculateStartDate(LocalDateTime now, RankingType type) {
        switch (type){
            case MONTHLY -> {
                return now.withDayOfMonth(1).toLocalDate().atStartOfDay();
            }
            case WEEKLY -> {
                return now
                        .with(DayOfWeek.MONDAY)
                        .toLocalDate()
                        .atStartOfDay();
            }
            default -> throw new IllegalArgumentException("LocalDateTime 지원하지 않는 타입");
        }
    }

     /**
     * type 값에 따라 이번주/이번달 마지막일 반환
     * @param now : 현재 날짜
     * @param type : 주 / 월 / 일
     * @return 이번주 / 이번달 마지막 날짜
     */
    @Override
    public LocalDateTime calculateEndDate(LocalDateTime now, RankingType type) {
        switch (type){
            case MONTHLY -> {
                return now.withDayOfMonth(now.toLocalDate().lengthOfMonth())
                        .toLocalDate()
                        .atTime(23,59,59);
            }
            case WEEKLY -> {
                return now.with(DayOfWeek.SUNDAY)
                        .toLocalDate()
                        .atTime(23,59,59);
            }
            default -> throw new IllegalArgumentException("LocalDateTime 지원하지 않는 타입");
        }
    }
}
