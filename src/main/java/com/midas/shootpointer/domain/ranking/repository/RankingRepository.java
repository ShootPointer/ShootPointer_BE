package com.midas.shootpointer.domain.ranking.repository;

import com.midas.shootpointer.domain.ranking.dto.RankingType;
import com.midas.shootpointer.domain.ranking.entity.RankingDocument;
import com.midas.shootpointer.domain.ranking.entity.RankingEntry;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface RankingRepository extends MongoRepository<RankingDocument, String> {
    //typePeriodKey로 조회
    RankingDocument findByTypePeriodKey(String typePeriodKey);

    //이번 주 / 이번 달 랭킹 Top10
    @Query("""
            SELECT new com.midas.shootpointer.domain.ranking.entity.RankingEntry(
                null,
                m.memberId,
                m.username,
                (SUM (h.twoPointCount)*2 + SUM(h.threePointCount)*3),
                (SUM (h.twoPointCount)*2),
                (SUM (h.threePointCount)*3)
                )
                FROM
                      Member as m
                JOIN
                      HighlightEntity as h ON m.memberId = h.member.memberId
                JOIN
                      PostEntity as p ON p.member.memberId = m.memberId
                WHERE
                     (
                         :type = com.midas.shootpointer.domain.ranking.dto.RankingType.WEEKLY
                         AND h.createdAt BETWEEN :startDate AND :endDate
                    )
                    OR
                    (
                        :type = com.midas.shootpointer.domain.ranking.dto.RankingType.MONTHLY
                        AND h.createdAt BETWEEN :start AND :endDate
                    )
                GROUP BY
                         m.memberId, m.username
                ORDER BY
                         SUM(h.twoPointCount)*2 + SUM(h.threePointCount)*3 DESC,
                         SUM(h.threePointCount)*3 DESC,
                         SUM(h.twoPointCount)*2 DESC
            """)
    List<RankingEntry> fetchThisWeekRanking_Top10(Pageable page, LocalDateTime startDate, LocalDateTime endDate, RankingType type);
}
