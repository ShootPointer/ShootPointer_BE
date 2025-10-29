package com.midas.shootpointer.domain.ranking.repository;

import com.midas.shootpointer.RedisTestContainer;
import com.midas.shootpointer.domain.member.entity.Member;
import com.midas.shootpointer.domain.ranking.dto.RankingResult;
import com.midas.shootpointer.domain.ranking.dto.RankingType;
import com.midas.shootpointer.domain.ranking.entity.RankingEntry;
import com.midas.shootpointer.domain.ranking.mapper.RankingMapper;
import com.midas.shootpointer.domain.ranking.mapper.RankingMapperImpl;
import com.midas.shootpointer.global.common.ErrorCode;
import com.midas.shootpointer.global.config.RedisConfig;
import com.midas.shootpointer.global.exception.CustomException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.test.context.TestPropertySource;

import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;

@Import({
        RedisTestContainer.class,
        RankingRedisRepository.class
})
@SpringBootTest(
        webEnvironment = SpringBootTest.WebEnvironment.NONE,
        classes = {RankingRedisRepository.class, RedisTestContainer.class, RedisConfig.class, RankingMapperImpl.class}
)
@TestPropertySource("classpath:application-test.yml")
class RankingRedisRepositoryTest {
    @Autowired
    private RankingRedisRepository redisRepository;

    @Autowired
    private RedisTemplate<String,Object> redisTemplate;

    @Autowired
    private RankingMapper rankingMapper;

    private static ZSetOperations<String,Object> zSetOperations;

    @Value("${spring.data.redis.custom.ranking.key.monthly}")
    private String monthlyPrefix;

    @Value("${spring.data.redis.custom.ranking.key.weekly}")
    private String weeklyPrefix;


    private final int TWO_WEIGHT=1;
    private final int THREE_WEIGHT=1_000;
    private final int TOTAL_WEIGHT=1_000_000;


    //메서드 실행 전 캐시 초기화
    @BeforeEach
    void setUp(){
        redisTemplate.afterPropertiesSet();
        redisTemplate.getConnectionFactory()
                .getConnection()
                .serverCommands()
                .flushAll();

        zSetOperations=redisTemplate.opsForZSet();
    }

    @Nested
    @DisplayName("랭킹 삽입 테스트")
    class insertRanking{
        @Test
        @DisplayName("redis에 기존 데이터가 존재하지 않는 경우 새로운 데이터를 삽입합니다.")
        void addOrUpdate_NOT_EXIST_DATA(){
            //given
            RankingType type=RankingType.MONTHLY;
            int newScore=140;
            UUID memberId=UUID.randomUUID();
            RankingResult result=new RankingResult(
                    "test1",memberId,140,50,90
            );
            String key=setKey(type);

            //when
            redisRepository.addOrUpdate(type,newScore,result);

            //then
            Set<ZSetOperations.TypedTuple<Object>> results=zSetOperations.rangeWithScores(key,0,-1);

            assertThat(results).isNotNull();
            assertThat(results).hasSize(1);

            ZSetOperations.TypedTuple<Object> tuple=results.iterator().next();
            System.out.println("tuple = "+tuple.getClass()+tuple);
            assertThat(tuple.getScore()).isEqualTo(newScore);

            //저장된 객체 검증
            RankingResult stored=rankingMapper.convertToRankingResult(tuple.getValue());
            assertThat(stored.memberId()).isEqualTo(memberId);
            assertThat(stored.memberName()).isEqualTo("test1");
            assertThat(stored.totalScore()).isEqualTo(140);
            assertThat(stored.threeScore()).isEqualTo(90);
            assertThat(stored.twoScore()).isEqualTo(50);
        }

        @Test
        @DisplayName("redis에 기존 데이터가 존재하고 새로운 RankingResult와 같은 멤버 id 이면 기존 데이터를 삭제하고 새로운 데이터를 삽입합니다.")
        void addOrUpdate_EXIST_DATA_AND_SAME_MEMBER_ID(){
            //given
            RankingType type=RankingType.MONTHLY;
            int score=140;
            int newScore=200;

            UUID memberId=UUID.randomUUID();
            RankingResult result=new RankingResult(
                    "test1",memberId,140,50,90
            );
            RankingResult newResult=new RankingResult(
                    "test1",memberId,190,100,90
            );

            String key=setKey(type);
            zSetOperations.add(key,result,score);

            //삭제 이전에 데이터 개수 검증
            assertThat(zSetOperations.size(key)).isEqualTo(1);

            //when
            redisRepository.addOrUpdate(type,newScore,newResult);

            //then
            Set<ZSetOperations.TypedTuple<Object>> results=zSetOperations.rangeWithScores(key,0,-1);

            assertThat(results).isNotNull();
            assertThat(results).hasSize(1);

            ZSetOperations.TypedTuple<Object> tuple=results.iterator().next();
            System.out.println("tuple = "+tuple.getClass()+tuple);
            assertThat(tuple.getScore()).isEqualTo(newScore);

            //저장된 객체 검증
            RankingResult stored=rankingMapper.convertToRankingResult(tuple.getValue());
            assertThat(stored.memberId()).isEqualTo(memberId);
            assertThat(stored.memberName()).isEqualTo("test1");
            assertThat(stored.totalScore()).isEqualTo(190);
            assertThat(stored.threeScore()).isEqualTo(90);
            assertThat(stored.twoScore()).isEqualTo(100);
        }
    }


    @Nested
    @DisplayName("랭킹 조회 테스트")
    class fetchRanking{
        @DisplayName("redis에서 객체 조회 시 값이 비어있거나 null 이면 빈 리스트의 RankingEntry를 반환합니다.")
        @Test
        void getHighlightsWeeklyRanking_IS_NULL_OR_IS_EMPTY(){
            //given
            RankingType type=RankingType.MONTHLY;

            //when
            List<RankingEntry> results=redisRepository.getHighlightsWeeklyRanking(type);

            //then
            assertThat(results).isEmpty();
            assertThat(results).isEqualTo(Collections.emptyList());
        }

        @DisplayName("redis에 저장된 top10 목록을 조회하고 List<RankingEntry> 형태로 반환합니다. - score 기준 내림차순")
        @Test
        void getHighlightsWeeklyRanking(){
            //given
            RankingType type=RankingType.MONTHLY;
            String key=setKey(type);
            List<RankingResult> insert= new ArrayList<>(makeRankingResults());
            //예상 멤버 이름
            List<String> expectedMemberName=List.of(
                    "test7",
                    "test3",
                    "test2",
                    "test10",
                    "test12",
                    "test11",
                    "test1",
                    "test9",
                    "test4",
                    "test6"
            );

            for (RankingResult result:insert){
                System.out.println(result.memberName());
            }
            for (RankingResult data:insert){
                zSetOperations.add(key,data,calculateRankingWeight(data.twoScore(),data.threeScore(),data.totalScore()));
            }


            //when
            List<RankingEntry> results=redisRepository.getHighlightsWeeklyRanking(type);
            //then
            assertThat(results).hasSize(10);

            for (int index=0;index<10;index++){
                RankingEntry entry=results.get(index);

                assertThat(entry).isNotNull();
                assertThat(entry.getRank()).isEqualTo(index+1);
                assertThat(entry.getMemberName()).isEqualTo(expectedMemberName.get(index));
            }
        }
    }

    @Nested
    @DisplayName("redis 데이터 삭제 테스트")
    class deleteRanking{
        @DisplayName("type key에 해당하는 데이터를 모두 삭제합니다.")
        @Test
        void deleteAll(){
            //given
            RankingType type=RankingType.MONTHLY;
            String key=setKey(type);
            List<RankingResult> insert= new ArrayList<>(makeRankingResults());
            for (RankingResult data:insert){
                zSetOperations.add(key,data,calculateRankingWeight(data.twoScore(),data.threeScore(),data.totalScore()));
            }

            //when
            redisRepository.deleteAll(type);
            Set<ZSetOperations.TypedTuple<Object>> results=zSetOperations.rangeWithScores(key,0,-1);

            //then
            assertThat(results).isEmpty();
        }
    }

    @Nested
    @DisplayName("Redis 랭킹 점수 차감 테스트")
    class deleteRankingScore {

        @Test
        @DisplayName("존재하는 멤버의 점수를 차감하고, 점수가 0보다 크면 업데이트된 점수로 유지됩니다.")
        void deleteRankingScore_SCORE_GREATER_THAN_ZERO() {
            // given
            RankingType type = RankingType.MONTHLY;
            String key = setKey(type);
            UUID memberId = UUID.randomUUID();

            RankingResult result = new RankingResult(
                    "test1", memberId, 200, 40, 160
            );

            // 초기 점수 계산: twoScore(40)*1 + threeScore(160)*1000 + totalScore(200)*1000000
            double initialScore = calculateRankingWeight(40, 160, 200);
            zSetOperations.add(key, result, initialScore);

            // 차감할 점수
            double deleteScore = 10 * TWO_WEIGHT;

            Member member = Member.builder()
                    .memberId(memberId)
                    .build();

            // when
            redisRepository.deleteRankingScore(type, member, deleteScore);

            // then
            Set<ZSetOperations.TypedTuple<Object>> results = zSetOperations.rangeWithScores(key, 0, -1);

            assertThat(results).isNotNull();
            assertThat(results).hasSize(1);

            ZSetOperations.TypedTuple<Object> tuple = results.iterator().next();
            double expectedScore = initialScore - deleteScore;

            assertThat(tuple.getScore()).isEqualTo(expectedScore);

            // 데이터는 여전히 존재해야 함
            RankingResult stored = rankingMapper.convertToRankingResult(tuple.getValue());
            assertThat(stored.memberId()).isEqualTo(memberId);
        }

        @Test
        @DisplayName("점수 차감 후 0 이하가 되면 해당 멤버의 데이터를 완전히 삭제합니다.")
        void deleteRankingScore_SCORE_ZERO_OR_LESS_REMOVE_DATA() {
            // given
            RankingType type = RankingType.WEEKLY;
            String key = setKey(type);
            UUID memberId = UUID.randomUUID();

            RankingResult result = new RankingResult(
                    "test1", memberId, 50, 20, 30
            );

            double initialScore = calculateRankingWeight(20, 30, 50);
            zSetOperations.add(key, result, initialScore);

            // 초기 점수보다 큰 값을 차감하여 0 이하로 만듦
            double deleteScore = initialScore + 100;

            Member member = Member.builder()
                    .memberId(memberId)
                    .build();

            // when
            redisRepository.deleteRankingScore(type, member, deleteScore);

            // then
            Set<ZSetOperations.TypedTuple<Object>> results = zSetOperations.rangeWithScores(key, 0, -1);

            assertThat(results).isEmpty();
        }

        @Test
        @DisplayName("존재하지 않는 멤버의 점수 차감 시도 시 아무 변화가 없습니다.")
        void deleteRankingScore_MEMBER_NOT_EXIST() {
            // given
            RankingType type = RankingType.MONTHLY;
            String key = setKey(type);
            UUID existMemberId = UUID.randomUUID();
            UUID notExistMemberId = UUID.randomUUID();

            RankingResult result = new RankingResult(
                    "test1", existMemberId, 200, 40, 160
            );

            double initialScore = calculateRankingWeight(40, 160, 200);
            zSetOperations.add(key, result, initialScore);

            double deleteScore = 50;

            Member notExistMember = Member.builder()
                    .memberId(notExistMemberId)
                    .build();

            // when
            redisRepository.deleteRankingScore(type, notExistMember, deleteScore);

            // then
            Set<ZSetOperations.TypedTuple<Object>> results = zSetOperations.rangeWithScores(key, 0, -1);

            assertThat(results).hasSize(1);

            ZSetOperations.TypedTuple<Object> tuple = results.iterator().next();

            // 점수가 변경되지 않아야 함
            assertThat(tuple.getScore()).isEqualTo(initialScore);

            RankingResult stored = rankingMapper.convertToRankingResult(tuple.getValue());
            assertThat(stored.memberId()).isEqualTo(existMemberId);
        }

        @Test
        @DisplayName("여러 멤버가 있을 때 특정 멤버의 점수만 차감됩니다.")
        void deleteRankingScore_MULTIPLE_MEMBERS() {
            // given
            RankingType type = RankingType.MONTHLY;
            String key = setKey(type);

            UUID memberId1 = UUID.randomUUID();
            UUID memberId2 = UUID.randomUUID();
            UUID memberId3 = UUID.randomUUID();

            RankingResult result1 = new RankingResult("test1", memberId1, 200, 40, 160);
            RankingResult result2 = new RankingResult("test2", memberId2, 300, 50, 250);
            RankingResult result3 = new RankingResult("test3", memberId3, 150, 30, 120);

            double score1 = calculateRankingWeight(40, 160, 200);
            double score2 = calculateRankingWeight(50, 250, 300);
            double score3 = calculateRankingWeight(30, 120, 150);

            zSetOperations.add(key, result1, score1);
            zSetOperations.add(key, result2, score2);
            zSetOperations.add(key, result3, score3);

            double deleteScore = 100;

            Member member2 = Member.builder()
                    .memberId(memberId2)
                    .build();

            // when
            redisRepository.deleteRankingScore(type, member2, deleteScore);

            // then
            Set<ZSetOperations.TypedTuple<Object>> results = zSetOperations.rangeWithScores(key, 0, -1);

            assertThat(results).hasSize(3);

            // member2의 점수만 변경되었는지 확인
            for (ZSetOperations.TypedTuple<Object> tuple : results) {
                RankingResult stored = rankingMapper.convertToRankingResult(tuple.getValue());

                if (stored.memberId().equals(memberId1)) {
                    assertThat(tuple.getScore()).isEqualTo(score1);
                } else if (stored.memberId().equals(memberId2)) {
                    assertThat(tuple.getScore()).isEqualTo(score2 - deleteScore);
                } else if (stored.memberId().equals(memberId3)) {
                    assertThat(tuple.getScore()).isEqualTo(score3);
                }
            }
        }

        @Test
        @DisplayName("빈 Redis 데이터에서 점수 차감 시도 시 예외가 발생하지 않습니다.")
        void deleteRankingScore_EMPTY_REDIS() {
            // given
            RankingType type = RankingType.WEEKLY;
            UUID memberId = UUID.randomUUID();
            double deleteScore = 50;

            Member member = Member.builder()
                    .memberId(memberId)
                    .build();

            // when & then - 예외가 발생하지 않아야 함
            redisRepository.deleteRankingScore(type, member, deleteScore);

            String key = setKey(type);
            Set<ZSetOperations.TypedTuple<Object>> results = zSetOperations.rangeWithScores(key, 0, -1);

            assertThat(results).isEmpty();
        }

        @Test
        @DisplayName("정확히 0이 되는 점수 차감 시 데이터가 삭제됩니다.")
        void deleteRankingScore_EXACTLY_ZERO() {
            // given
            RankingType type = RankingType.MONTHLY;
            String key = setKey(type);
            UUID memberId = UUID.randomUUID();

            RankingResult result = new RankingResult("test1", memberId, 100, 20, 80);

            double initialScore = calculateRankingWeight(20, 80, 100);
            zSetOperations.add(key, result, initialScore);

            // 정확히 초기 점수만큼 차감
            double deleteScore = initialScore;

            Member member = Member.builder()
                    .memberId(memberId)
                    .build();

            // when
            redisRepository.deleteRankingScore(type, member, deleteScore);

            // then
            Set<ZSetOperations.TypedTuple<Object>> results = zSetOperations.rangeWithScores(key, 0, -1);

            assertThat(results).isEmpty();
        }
    }
    private String setKey(RankingType type){
        switch (type){
            case MONTHLY -> {
                return monthlyPrefix;
            }
            case WEEKLY -> {
                return weeklyPrefix;
            }
        }
        throw new CustomException(ErrorCode.IS_NOT_VALID_RANKING_TYPE);
    }

    private List<RankingResult> makeRankingResults(){
        UUID memberId1=UUID.randomUUID();
        UUID memberId2=UUID.randomUUID();
        UUID memberId3=UUID.randomUUID();
        UUID memberId4=UUID.randomUUID();
        UUID memberId5=UUID.randomUUID();
        UUID memberId6=UUID.randomUUID();
        UUID memberId7=UUID.randomUUID();
        UUID memberId8=UUID.randomUUID();
        UUID memberId9=UUID.randomUUID();
        UUID memberId10=UUID.randomUUID();
        UUID memberId11=UUID.randomUUID();
        UUID memberId12=UUID.randomUUID();
        UUID memberId13=UUID.randomUUID();

        return List.of(
                new RankingResult("test1",memberId1,200,40,160),
                new RankingResult("test2",memberId2,400,200,200),
                new RankingResult("test3",memberId3,500,440,60),
                new RankingResult("test4",memberId4,100,40,60),
                new RankingResult("test5",memberId5,10,10,0),
                new RankingResult("test6",memberId6,21,11,10),
                new RankingResult("test7",memberId7,500,420,80),
                new RankingResult("test8",memberId8,20,10,10),
                new RankingResult("test9",memberId9,100,30,70),
                new RankingResult("test10",memberId10,240,80,160),
                new RankingResult("test11",memberId11,210,50,160),
                new RankingResult("test12",memberId12,230,10,220),
                new RankingResult("test13",memberId13,10,10,0)
                );
        /*========================================= [ Scenario ] ==========================================
        *   1. totalScore가 가장 높은 순서 -> threeScore가 높은 순서 -> twoScore가 높은 순서 상위 10명 추출
        *   2. 예상 결과
        *       test7
                test3
                test2
                test10
                test12
                test11
                test1
                test9
                test4
                test6
        */
    }

    public double calculateRankingWeight(int twoScore, int threeScore,int totalScore) {
        return (double) twoScore*TWO_WEIGHT + (double) threeScore*THREE_WEIGHT + (double)totalScore *TOTAL_WEIGHT ;
    }
}