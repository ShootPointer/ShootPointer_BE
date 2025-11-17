package com.midas.shootpointer.domain.ranking.repository;

import com.midas.shootpointer.domain.ranking.dto.RankingType;
import com.midas.shootpointer.domain.ranking.entity.RankingEntry;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class RankingJpaRepositoryImpl implements RankingJpaRepository{
    @PersistenceContext
    private final EntityManager em;

    @Override
    public List<RankingEntry> fetchThisWeekRankingTop10(LocalDateTime startDate, LocalDateTime endDate, RankingType type) {
        String sql= """
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
                        AND h.createdAt BETWEEN :startDate AND :endDate
                    )
                GROUP BY
                         m.memberId, m.username
                ORDER BY
                         SUM(h.twoPointCount)*2 + SUM(h.threePointCount)*3 DESC,
                         SUM(h.threePointCount)*3 DESC,
                         SUM(h.twoPointCount)*2 DESC
                """;

        return em.createQuery(sql,RankingEntry.class)
                .setParameter("startDate",startDate)
                .setParameter("endDate",endDate)
                .setParameter("type",type)
                .setMaxResults(10)
                .getResultList();
    }
}
