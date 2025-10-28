package com.midas.shootpointer.domain.ranking.repository;

import com.midas.shootpointer.domain.ranking.dto.RankingResult;
import com.midas.shootpointer.domain.ranking.dto.RankingType;
import com.midas.shootpointer.domain.ranking.entity.RankingEntry;
import com.midas.shootpointer.domain.ranking.mapper.RankingMapper;
import com.midas.shootpointer.global.common.ErrorCode;
import com.midas.shootpointer.global.exception.CustomException;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.stereotype.Repository;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@RequiredArgsConstructor
@Repository
public class RankingRedisRepository {
    @Value("${spring.data.redis.custom.ranking.key.weekly}")
    private String weeklyKeyPrefix;

    @Value("${spring.data.redis.custom.ranking.key.monthly}")
    private String monthlyKeyPrefix;

    @Value("${spring.data.redis.custom.ranking.TTL.weekly}")
    private Long weeklyTTL;

    @Value("${spring.data.redis.custom.ranking.TTL.monthly}")
    private Long monthlyTTL;

    private final RedisTemplate<String, Object> redisTemplate;
    private final RankingMapper mapper;
    private ZSetOperations<String, Object> zSetOperations;

    private final int START = 0;
    private final int END = 9;


    @PostConstruct
    private void init() {
        zSetOperations = redisTemplate.opsForZSet();
    }

    /**
     * 레디스 객체 등록
     */
    public void addOrUpdate(RankingType type, int newScore, RankingResult newResult) {
        //1. key 결정
        String key = setKey(type);

        //2. 기존 데이터 검색
        Set<ZSetOperations.TypedTuple<Object>> existedData = zSetOperations.rangeWithScores(key, 0, -1);


        if (existedData != null) {
            for (ZSetOperations.TypedTuple<Object> tuple : existedData) {
                Object value = tuple.getValue();
                RankingResult result = mapper.convertToRankingResult(value);

                //3. 기존 데이터가 존재하는 경우
                if (result != null && result.memberId().equals(newResult.memberId())) {
                    //4. 기존 데이터 삭제
                    zSetOperations.remove(key, value);
                    break;
                }
            }
        }

        //5. 새로운 데이터 삽입
        zSetOperations.add(key, newResult, newScore);

        //6. TTL 설정
        if (!redisTemplate.hasKey(key)){
            if (type.equals(RankingType.WEEKLY)) redisTemplate.expire(key, Duration.ofDays(7));
            else if (type.equals(RankingType.MONTHLY)) redisTemplate.expire(key,Duration.ofDays(31));
        }
    }

    /**
     * top10 랭킹 조회
     */
    public List<RankingEntry> getHighlightsWeeklyRanking(RankingType type) {
        String key = setKey(type);


        List<RankingEntry> results = new ArrayList<>();

        //레디스 랭킹 조회
        Set<ZSetOperations.TypedTuple<Object>> rankingSet = zSetOperations.reverseRangeWithScores(key, START, END);

        //값이 비어 있는 경우
        if (rankingSet == null || rankingSet.isEmpty()) return results;

        int rank = 1;

        for (ZSetOperations.TypedTuple<Object> tuple : rankingSet) {
            Object value = tuple.getValue();
            RankingEntry result=mapper.convertToRankingEntry(value);
            results.add(RankingEntry.builder()
                        .memberId(result.getMemberId())
                        .totalScore(result.getTotalScore())
                        .twoScore(result.getTwoScore())
                        .threeScore(result.getThreeScore())
                        .memberName(result.getMemberName())
                        .rank(rank)
                        .build()
            );
            rank++;
        }
        return results;
    }

    /**
     * 레디스 초기화
     */
    public void deleteAll(RankingType type) {
        String key = setKey(type);
        zSetOperations.removeRange(key, 0, -1);
    }

    private String setKey(RankingType type) {
        switch (type) {
            case MONTHLY -> {
                return monthlyKeyPrefix;
            }
            case WEEKLY -> {
                return weeklyKeyPrefix;
            }
        }
        throw new CustomException(ErrorCode.IS_NOT_VALID_RANKING_TYPE);
    }
}
