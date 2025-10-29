package com.midas.shootpointer.domain.ranking.repository;

import com.midas.shootpointer.RedisTestContainer;
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
class RankingRedisRepositoryTest  {
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