package com.midas.shootpointer.batch;

import com.midas.shootpointer.domain.ranking.dto.RankingType;
import com.midas.shootpointer.domain.ranking.entity.RankingDocument;
import com.midas.shootpointer.domain.ranking.entity.RankingEntry;
import com.midas.shootpointer.domain.ranking.repository.RankingRepository;
import jakarta.annotation.PostConstruct;
import org.junit.jupiter.api.*;
import org.springframework.batch.core.*;
import org.springframework.batch.test.JobLauncherTestUtils;
import org.springframework.batch.test.context.SpringBatchTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ActiveProfiles;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * ranking batch 통합 테스트
 */
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@ActiveProfiles("test")
@SpringBootTest
@SpringBatchTest
public class RankingBatchJobTest {
    @Autowired
    private JobLauncherTestUtils jobLauncherTestUtils;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private Job rankingJob;

    @Autowired
    private RankingRepository rankingRepository;
    @Autowired
    private MongoTemplate mongoTemplate;

    @PostConstruct
    public void configureJobLauncherTestUtils() throws Exception {
        jobLauncherTestUtils.setJob(rankingJob);
    }

    /**
     * 하이라이트 더미 데이터 삽입
     */
    @BeforeAll
    void setUpData() throws IOException {
        String sql = Files.readString(Paths.get("src/test/resources/sql/CreateHighlightDummyData.sql"));
        jdbcTemplate.execute(sql);
    }

    @AfterAll
    void cleanUp() {
        rankingRepository.deleteAll();
    }

    @Test
    @DisplayName("ranking job - weekly 통합 테스트를 진행합니다.")
    @Rollback(false)
    void rankingJobSuccessfully() throws Exception {
        //given
        // 이번 주 일요일 기준 00시 00분
        LocalDateTime dateTime = LocalDateTime.now();
        LocalDateTime sunDay = dateTime
                .with(DayOfWeek.MONDAY)
                .withHour(0)
                .withMinute(0)
                .withSecond(0)
                .withNano(0);
        // 오늘이 월요일 이후라면 (즉, 일요일 오전 이후면) 그날 00시로 맞추기
        if (dateTime.getDayOfWeek() != DayOfWeek.MONDAY) {
            sunDay = dateTime
                    .with(java.time.temporal.TemporalAdjusters.previous(DayOfWeek.MONDAY))
                    .withHour(0)
                    .withMinute(0)
                    .withSecond(0)
                    .withNano(0);
        }

        JobParameters jobParameters = jobLauncherTestUtils.getUniqueJobParametersBuilder()
                .addString("rankingType", RankingType.WEEKLY.name())
                .addString("end", sunDay.toString())
                .toJobParameters();

        //when - weekly job 실행
        JobExecution jobExecution = jobLauncherTestUtils.launchJob(jobParameters);

        //then
        //실행 여부 검증
        assertThat(jobExecution.getStatus()).isEqualTo(BatchStatus.COMPLETED);
        assertThat(jobExecution.getExitStatus()).isEqualTo(ExitStatus.COMPLETED);

        /**
         * =======================================  [ 실제 값 검증 ] =======================================
         */

        //1. 실제 DB 데이터 값 불러오기.
        //PostgreDB
        System.out.println("============= 실제 데이터 검증 =============");
        String sql = Files.readString(Paths.get("src/test/resources/sql/VerificationHighlightData.sql"));

        LocalDateTime start = sunDay.minusDays(7); // 월요일 00:00:00
        LocalDateTime end = sunDay;

        System.out.println(start);
        System.out.println(end);

        List<RankingResult> results = jdbcTemplate.query(sql,
                ps->{
                    ps.setObject(1,start);
                    ps.setObject(2,end);
                },
                (rs, rowNum) -> new RankingResult(
                        rs.getString("member_name"),
                        rs.getObject("member_id",UUID.class),
                        rs.getInt("two_total"),
                        rs.getInt("three_total"),
                        rs.getInt("total"),
                        null
                )
        );

        System.out.println("=================result================");
        for (RankingResult result:results){
            System.out.println(result.memberId);
            System.out.println(result.threeTotal);
            System.out.println(result.twoTotal);
            System.out.println(result.total);
        }

        System.out.println("=======================================");
        //MongoDB
        String findKey = String.format(
                "WEEKLY_%d-W%d",
                sunDay.minusDays(1).getYear(),
                sunDay.minusDays(1).get(java.time.temporal.WeekFields.ISO.weekOfYear())
        );
        RankingDocument document= rankingRepository.findByTypePeriodKey(findKey)
                .orElse(null);

        //결과 값이 있는 경우
        if (document!=null){
            assertThat(document.getPeriodBegin()).isEqualTo(sunDay);
            assertThat(document.getType()).isEqualTo(RankingType.WEEKLY);
            assertThat(document.getTypePeriodKey()).startsWith("WEEKLY");

            List<RankingEntry> top10=document.getTop10();
            int rank=1;
            int idx=0;
            for (RankingEntry entry:top10){
                assertThat(entry.getRank()).isEqualTo(rank);
                assertThat(entry.getMemberId()).isEqualTo(results.get(idx).memberId);
                assertThat(entry.getMemberName()).isEqualTo(results.get(idx).memberName);
                assertThat(entry.getTotalScore()).isEqualTo(results.get(idx).total);
                assertThat(entry.getThreeScore()).isEqualTo(results.get(idx).threeTotal);
                assertThat(entry.getTwoScore()).isEqualTo(results.get(idx).twoTotal);

                idx++;
                rank++;
            }
        }
    }

    /**
     * 검증 쿼리용 클래스
     */
    record RankingResult (
        String memberName,
        UUID memberId,
        int twoTotal,
        int threeTotal,
        int total,
        LocalDateTime createdAt
    ){}
}
