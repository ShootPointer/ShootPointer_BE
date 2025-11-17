package com.midas.shootpointer.domain.ranking.repository;


import com.midas.shootpointer.domain.ranking.entity.RankingEntry;
import com.midas.shootpointer.domain.ranking.entity.RankingType;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Repository
@RequiredArgsConstructor
@Transactional(readOnly = true)
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
               CAST(SUM(h.twoPointCount * 2 + h.threePointCount * 3) AS integer),
               CAST(SUM(h.twoPointCount * 2) AS integer),
               CAST(SUM(h.threePointCount * 3) AS integer)
                )
                FROM
                      Member as m
                JOIN
                      HighlightEntity as h ON m.memberId = h.member.memberId
                JOIN
                      PostEntity as p ON p.member.memberId = m.memberId
                WHERE
                     (
                         :type = 'WEEKLY'
                         AND h.createdAt BETWEEN :startDate AND :endDate
                    )
                    OR
                    (
                        :type = 'MONTHLY'
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
                .setParameter("type",type.name())
                .setMaxResults(10)
                .getResultList();
    }
}
