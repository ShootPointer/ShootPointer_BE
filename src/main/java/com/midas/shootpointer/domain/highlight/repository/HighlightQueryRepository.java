package com.midas.shootpointer.domain.highlight.repository;

import com.midas.shootpointer.domain.highlight.entity.HighlightEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;
@Repository
public interface HighlightQueryRepository extends JpaRepository<HighlightEntity, UUID> {
    Optional<HighlightEntity> findByHighlightId(UUID highlightId);

    @Query("SELECT COUNT (H.highlightId)>0 " +
            "FROM HighlightEntity AS H " +
            "WHERE H.highlightId = :highlightId AND H.member.memberId = :memberId")
    boolean isMembersHighlight(@Param("memberId") UUID memberId, @Param("highlightId") UUID highlightId);


    @Query(value = "SELECT EXISTS(SELECT * FROM member AS M left join highlight AS H " +
                   "WHERE M.member_id=:memberId " +
                   "AND H.highlight_id=:highlightId ) ",nativeQuery = true)
    boolean existsByHighlightIdAndMember(@Param("highlightId") UUID highlightId, @Param("memberId") UUID memberId);

    boolean existsByHighlightId(UUID highlightId);

    @Query(value = """
            SELECT h
            FROM HighlightEntity as h
            INNER JOIN
            Member as m ON h.member.memberId = m.memberId
            WHERE m.isAggregationAgreed = true
              AND h.isSelected = true
              AND m.memberId =:memberId
            ORDER BY h.createdAt DESC
        """)
    Page<HighlightEntity> fetchAllMembersHighlights(@Param("memberId")UUID memberId, Pageable pageable);
    
    
    /** ===========================
     *
     *  마이페이지 부분
     *
     * ============================
     */
    // 2점슛 카운트 총합
    @Query("SELECT COALESCE(SUM(h.twoPointCount), 0) FROM HighlightEntity h WHERE h.member.memberId = :memberId AND h.isSelected = true")
    Integer sumTwoPointCountByMemberId(@Param("memberId") UUID memberId);
    
    // 3점슛 카운트 총합
    @Query("SELECT COALESCE(SUM(h.threePointCount), 0) FROM HighlightEntity h WHERE h.member.memberId = :memberId AND h.isSelected = true")
    Integer sumThreePointCountByMemberId(@Param("memberId") UUID memberId);
    
    // 하이라이트 개수
    @Query("SELECT COUNT(h) FROM HighlightEntity h WHERE h.member.memberId = :memberId AND h.isSelected = true")
    Integer countByMemberId(@Param("memberId") UUID memberId);
}