package com.midas.shootpointer.domain.ranking.helper;

import com.midas.shootpointer.domain.ranking.dto.RankingResult;
import com.midas.shootpointer.domain.ranking.dto.RankingType;
import com.midas.shootpointer.domain.ranking.entity.RankingDocument;
import com.midas.shootpointer.domain.ranking.repository.RankingRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementSetter;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.test.util.ReflectionTestUtils;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RankingUtilImplTest {
    @InjectMocks
    private RankingUtilImpl rankingUtil;

    @Mock
    private RankingRepository rankingRepository;

    @Mock
    private JdbcTemplate jdbcTemplate;

    @Test
    @DisplayName("Period Key로 랭킬 Document를 조회합니다.")
    void fetchRankingDocumentByPeriodKey() {
        //given
        LocalDateTime date=LocalDateTime.now();
        RankingDocument document=RankingDocument.of(Collections.emptyList(), date, RankingType.WEEKLY);
        String periodKey="periodKey";

        when(rankingRepository.findByTypePeriodKey(any(String.class))).thenReturn(document);

        //when
        RankingDocument response=rankingUtil.fetchRankingDocumentByPeriodKey(periodKey);

        //then
        verify(rankingRepository,times(1)).findByTypePeriodKey(periodKey);
        assertThat(response).isNotNull();
        assertThat(response.getType()).isEqualTo(RankingType.WEEKLY);
    }

    @Test
    @DisplayName("RankingDocument의 index 값인 type period key를 생성합니다._MONTHLY")
    void getRankingTypeKey_MONTHLY() {
        //given
        RankingType monthly=RankingType.MONTHLY;
        LocalDateTime now =LocalDateTime.of(
                2025,10,21,0,32,10,0
        );
        String expectedResult="MONTHLY_2025-09";

        //when
        String result=rankingUtil.getRankingTypeKey(monthly,now);

        //then
        assertThat(expectedResult).isEqualTo(result);
    }

    @Test
    @DisplayName("RankingDocument의 index 값인 type period key를 생성합니다._WEEKLY")
    void getRankingTypeKey_WEEKLY() {
        //given
        RankingType weekly=RankingType.WEEKLY;
        LocalDateTime now =LocalDateTime.of(
                2025,10,21,0,32,10,0
        );
        String expectedResult="WEEKLY_2025-W42";

        //when
        String result=rankingUtil.getRankingTypeKey(weekly,now);

        //then
        assertThat(result).isEqualTo(expectedResult);
    }

    @Test
    @DisplayName("RankingDocument의 index 값인 type period key를 생성합니다._DAILY")
    void getRankingTypeKey_DAILY() {
        //given
        RankingType daily=RankingType.DAILY;
        LocalDateTime now=LocalDateTime.of(
                2025,10,21,0,32,10,0
        );
        String expectedResult="DAILY_2025-10-20";

        //when
        String result=rankingUtil.getRankingTypeKey(daily,now);

        //then
        assertThat(result).isEqualTo(expectedResult);
    }

    @Test
    @DisplayName("start ~ end 기간 사이의 하이라이트 랭킹 정보를 조회하고 2점 점수 합, 3점 점수 합, 총 점수 합을 집계하여 조회합니다.")
    void fetchRankingResult() throws IOException {
        //given
        ReflectionTestUtils.setField(rankingUtil,"queryPath","src/test/resources/query/ranking.sql");
        LocalDateTime start=LocalDateTime.of(2025,1,1,0,0);
        LocalDateTime end=LocalDateTime.of(2025,1,31,0,0);

        UUID memberId1=UUID.randomUUID();
        UUID memberId2=UUID.randomUUID();
        UUID memberId3=UUID.randomUUID();
        UUID memberId4=UUID.randomUUID();
        UUID memberId5=UUID.randomUUID();

        List<RankingResult> mockResults=List.of(
                new RankingResult("test1",memberId1,50,40,10),
                new RankingResult("test2", memberId2,40,40,0),
                new RankingResult("test3", memberId3,30,10,20),
                new RankingResult("test4", memberId4,20,10,10),
                new RankingResult("test5",memberId5,10,5,5)
        );

        //when
        when(jdbcTemplate.query(anyString(),any(PreparedStatementSetter.class),any(RowMapper.class))).thenReturn(mockResults);

        List<RankingResult> result=rankingUtil.fetchRankingResult(start,end);

        //then
        assertThat(result).hasSize(5);

        assertThat(result.get(0).memberId()).isEqualTo(memberId1);
        assertThat(result.get(0).totalScore()).isEqualTo(50);
        assertThat(result.get(0).threeScore()).isEqualTo(10);
        assertThat(result.get(0).twoScore()).isEqualTo(40);

        assertThat(result.get(1).memberId()).isEqualTo(memberId2);
        assertThat(result.get(1).totalScore()).isEqualTo(40);
        assertThat(result.get(1).threeScore()).isEqualTo(0);
        assertThat(result.get(1).twoScore()).isEqualTo(40);

        assertThat(result.get(2).memberId()).isEqualTo(memberId3);
        assertThat(result.get(2).totalScore()).isEqualTo(30);
        assertThat(result.get(2).threeScore()).isEqualTo(20);
        assertThat(result.get(2).twoScore()).isEqualTo(10);

        assertThat(result.get(3).memberId()).isEqualTo(memberId4);
        assertThat(result.get(3).totalScore()).isEqualTo(20);
        assertThat(result.get(3).threeScore()).isEqualTo(10);
        assertThat(result.get(3).twoScore()).isEqualTo(10);

        assertThat(result.get(4).memberId()).isEqualTo(memberId5);
        assertThat(result.get(4).totalScore()).isEqualTo(10);
        assertThat(result.get(4).threeScore()).isEqualTo(5);
        assertThat(result.get(4).twoScore()).isEqualTo(5);

        verify(jdbcTemplate).query(
                anyString(),
                any(PreparedStatementSetter.class),
                any(RowMapper.class)
        );
    }

    @Test
    @DisplayName("RankingType 별로 집계 시작일을 계산합니다._MONTHLY")
    void getBeginTime_MONTHLY() {
        //given
        LocalDateTime end=LocalDateTime.of(
                2025,11,5,5,55,55,55
        );
        LocalDateTime expectedDate=LocalDateTime.of(
                2025,10,1,0,0,0,0
        );

        RankingType monthly=RankingType.MONTHLY;

        //when
        LocalDateTime resultTime=rankingUtil.getBeginTime(end,monthly);

        //then
        assertThat(resultTime).isEqualTo(expectedDate);
    }

    @Test
    @DisplayName("RankingType 별로 집계 시작일을 계산합니다._WEEKLY")
    void getBeginTime_WEEKLY(){
        //given
        LocalDateTime end=LocalDateTime.of(
                2025,11,5,5,55,55,55
        );
        LocalDateTime expectedDate=LocalDateTime.of(
                2025,10,27,0,0,0,0
        );

        RankingType weekly=RankingType.WEEKLY;

        //when
        LocalDateTime resultTime=rankingUtil.getBeginTime(end,weekly);

        //then
        assertThat(resultTime).isEqualTo(expectedDate);
    }

    @Test
    @DisplayName("RankingType 별로 집계 시작일을 계산합니다._DAILY")
    void getBeginTime_DAILY(){
        //given
        LocalDateTime end=LocalDateTime.of(
                2025,11,5,5,55,55,55
        );
        LocalDateTime expectedDate=LocalDateTime.of(
                2025,11,4,0,0,0,0
        );

        RankingType daily=RankingType.DAILY;

        //when
        LocalDateTime resultTime=rankingUtil.getBeginTime(end,daily);

        //then
        assertThat(resultTime).isEqualTo(expectedDate);

    }

    @Test
    @DisplayName("랭킹 점수 가중치를 포함하여 계산합니다.")
    void calculateRankingWeight(){
        //given
        int twoScore=14;
        int threeScore=21;
        int totalScore=threeScore+twoScore;

        //when
        double weight=rankingUtil.calculateRankingWeight(twoScore,threeScore,totalScore);

        //then
        assertThat(weight).isEqualTo(twoScore+1_000*threeScore+1_000_000*totalScore);
    }
}