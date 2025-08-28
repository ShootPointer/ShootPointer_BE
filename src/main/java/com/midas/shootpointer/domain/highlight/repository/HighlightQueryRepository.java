package com.midas.shootpointer.domain.highlight.repository;

import com.midas.shootpointer.domain.highlight.entity.HighlightEntity;
import com.midas.shootpointer.domain.member.entity.Member;
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

    Optional<HighlightEntity> findByHighlightKey(UUID highlightKey);

    @Query(value = "SELECT EXISTS(SELECT * FROM member AS M left join highlight AS H " +
                   "WHERE M.member_id=:memberId " +
                   "AND H.highlight_id=:highlightId ) ",nativeQuery = true)
    boolean existsByHighlightIdAndMember(@Param("highlightId") UUID highlightId, @Param("memberId") UUID memberId);
}