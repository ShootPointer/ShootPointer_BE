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

    @AfterEach
    void cleanUp() {
        //rankingRepository.deleteAll();
    }

    @Test
    @DisplayName("ranking job - weekly 통합 테스트를 진행합니다.")
    void rankingJobSuccessfully_WEEKLY() throws Exception {
        testRankingJob(RankingType.WEEKLY);
    }

    @Test
    @DisplayName("ranking job - monthly 통합 테스트를 진행합니다.")
    void rankingJobSuccessfully_MONTHLY() throws Exception {
        testRankingJob(RankingType.MONTHLY);
    }

    @Test
    @DisplayName("ranking job - daily 통합 테스트를 진행합니다.")
    void rankingJobSuccessfully_DAILY() throws Exception {
        testRankingJob(RankingType.DAILY);
    }

    /**
     * ========================= 유틸 메서드 =========================
     */
    private void testRankingJob(RankingType type) throws Exception {
        LocalDateTime end=getPeriodEnd(type);
        LocalDateTime start=getPeriodStart(type,end);

        System.out.println("================== [ "+type+" ] ==================");
        System.out.println("Start   :"+start);
        System.out.println("End     :"+end);


        JobParameters jobParameters = jobLauncherTestUtils.getUniqueJobParametersBuilder()
                .addString("rankingType", type.name())
                .addString("end", end.toString())
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
        String findKey = switch (type) {
            case DAILY   -> String.format("DAILY_%s", start.toLocalDate());
            case WEEKLY  -> String.format("WEEKLY_%d-W%d",
                    start.getYear(),
                    start.get(java.time.temporal.WeekFields.ISO.weekOfYear()));
            case MONTHLY -> String.format("MONTHLY_%d-%02d",
                    start.getYear(),
                    start.getMonthValue());
        };


        RankingDocument document= rankingRepository.findByTypePeriodKey(findKey)
                .orElse(null);
        assertThat(document).isNotNull();
        assertThat(document.getType()).isEqualTo(type);
        assertThat(document.getPeriodBegin()).isEqualTo(start);

        //결과 값이 있는 경우
        List<RankingEntry> top10 = document.getTop10();
        for (int i = 0; i < top10.size(); i++) {
            RankingEntry entry = top10.get(i);
            RankingResult result = results.get(i);

            assertThat(entry.getRank()).isEqualTo(i + 1);
            assertThat(entry.getMemberId()).isEqualTo(result.memberId);
            assertThat(entry.getTotalScore()).isEqualTo(result.total);
        }
    }

    private LocalDateTime getPeriodEnd(RankingType type) {
        LocalDateTime now = LocalDateTime.now().withHour(0).withMinute(0).withSecond(0).withNano(0);
        return switch (type){
            case DAILY   -> now;                         // 오늘 00:00
            case WEEKLY  -> now.with(DayOfWeek.MONDAY);  // 이번주 월 00:00
            case MONTHLY -> now.withDayOfMonth(1);       // 이번달 1일 00:00
        };
    }

    private LocalDateTime getPeriodStart(RankingType type, LocalDateTime end) {
        return switch (type){
            case DAILY   -> end.minusDays(1);   // 어제 00:00
            case WEEKLY  -> end.minusDays(7);   // 지난주 월 00:00
            case MONTHLY -> end.minusMonths(1); // 지난달 1일 00:00
        };
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
