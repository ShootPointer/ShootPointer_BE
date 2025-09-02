package com.midas.shootpointer.domain.like.repository;

import com.midas.shootpointer.domain.like.entity.LikeEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;
@Repository
public interface LikeQueryRepository extends JpaRepository<LikeEntity,Long> {
    @Query(value = "SELECT EXISTS( SELECT * FROM member AS M INNER JOIN post AS P INNER JOIN like_table as L " +
                   "WHERE M.member_id=:memberId AND P.post_id=:postId)",nativeQuery = true)
    boolean existByMemberIdAndPostId(@Param("memberId") UUID memberId, @Param("postId") Long postId);

    @Query(value = "SELECT * FROM like_table AS L WHERE L.member_id=:memberId AND L.post_id=:postId",
            nativeQuery = true
    )
    Optional<LikeEntity> findByPostIdAndMemberId(@Param("postId") Long postId,@Param("memberId") UUID memberId);
}
