package com.midas.shootpointer.domain.ranking.repository;

import com.midas.shootpointer.RedisTestContainer;
import com.midas.shootpointer.domain.ranking.dto.RankingResult;
import com.midas.shootpointer.domain.ranking.dto.RankingType;
import com.midas.shootpointer.domain.ranking.mapper.RankingMapper;
import com.midas.shootpointer.domain.ranking.mapper.RankingMapperImpl;
import com.midas.shootpointer.global.common.ErrorCode;
import com.midas.shootpointer.global.config.RedisConfig;
import com.midas.shootpointer.global.exception.CustomException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.test.context.TestPropertySource;

import java.util.Set;
import java.util.UUID;

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
        assertThat(stored.total()).isEqualTo(140);
        assertThat(stored.threeTotal()).isEqualTo(90);
        assertThat(stored.twoTotal()).isEqualTo(50);
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
        assertThat(stored.total()).isEqualTo(190);
        assertThat(stored.threeTotal()).isEqualTo(90);
        assertThat(stored.twoTotal()).isEqualTo(100);
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
}