package com.midas.shootpointer.domain.ranking.repository;

import com.midas.shootpointer.domain.member.entity.Member;
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

    private final RedisTemplate<String, Object> redisTemplate;
    private final RankingMapper mapper;
    private ZSetOperations<String, Object> zSetOperations;

    private final int START = 0;
    private final int END = 9;


    /**
     * Initialize ZSetOperations using the configured RedisTemplate.
     *
     * This method obtains the Redis ZSetOperations instance so the repository can perform sorted-set operations.
     */
    @PostConstruct
    private void init() {
        zSetOperations = redisTemplate.opsForZSet();
    }

    /**
     * Insert or update a ranking entry in the Redis sorted set for the specified ranking type.
     *
     * If an existing entry with the same member ID is present it will be replaced with the provided
     * entry and score. When the key is newly created, a TTL is applied based on the ranking type.
     *
     * @param type the ranking category (e.g., WEEKLY or MONTHLY) used to determine the Redis key and TTL
     * @param newScore the score to assign to the provided ranking entry
     * @param newResult the ranking entry to store; its memberId is used to detect and replace existing entries
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
     * Decrements a member's ranking score for the given ranking type and removes the entry if the resulting score is less than or equal to zero.
     *
     * @param type the ranking category (WEEKLY or MONTHLY) whose sorted set will be modified
     * @param member the member whose score will be decremented (matched by memberId)
     * @param deleteScore the amount to subtract from the member's current score; if the resulting score is less than or equal to zero the entry is removed
     */
    public void deleteRankingScore(RankingType type,Member member,double deleteScore){
        String key=setKey(type);

        Set<ZSetOperations.TypedTuple<Object>> existedData = zSetOperations.rangeWithScores(key, 0, -1);

        if (existedData!=null){
            for (ZSetOperations.TypedTuple<Object> tuple : existedData) {
                Object value=tuple.getValue();

                if (value==null) continue;

                RankingResult result=mapper.convertToRankingResult(value);

                //2. 기존 데이터가 존재하는 경우
                if (result != null && result.memberId().equals(member.getMemberId())) {
                    Double nowScore=tuple.getScore();

                    if (nowScore!=null){
                        Double newScore=zSetOperations.incrementScore(key,value,-deleteScore);
                        //3. 점수가 0이하면 제거
                        if (newScore!=null && newScore<=0){
                            zSetOperations.remove(key,value);
                        }
                    }

                    break;
                }
            }
        }
    }


    /**
     * Retrieve the top 10 ranking entries for the specified ranking type in descending score order.
     *
     * @param type the ranking type (e.g., WEEKLY or MONTHLY) used to select the Redis sorted set key
     * @return a list of up to 10 RankingEntry objects with populated fields and a 1-based `rank`; empty if no entries exist
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
     * Removes all entries from the Redis sorted set for the given ranking type.
     *
     * @param type the ranking type whose Redis sorted set will be cleared (WEEKLY or MONTHLY)
     */
    public void deleteAll(RankingType type) {
        String key = setKey(type);
        zSetOperations.removeRange(key, 0, -1);
    }

    /**
     * Resolve the Redis key prefix for the given ranking type.
     *
     * @param type the ranking type; expected values are `WEEKLY` or `MONTHLY`
     * @return the Redis key prefix associated with the provided ranking type
     * @throws CustomException if the provided `type` is null or not a recognized ranking type (ErrorCode.IS_NOT_VALID_RANKING_TYPE)
     */
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