package com.midas.shootpointer.domain.like.repository;

import com.midas.shootpointer.domain.like.entity.LikeEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface LikeCommandRepository extends JpaRepository<LikeEntity,Long> {
    @Modifying
    @Query(value = "UPDATE post SET like_cnt = like_cnt + 1 WHERE post_id=:postId",nativeQuery = true)
    void increasesLikeCnt(@Param(value = "postId")Long postId);
}
