package com.midas.shootpointer.domain.post.repository;

import com.midas.shootpointer.domain.post.entity.PostEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface PostCommandRepository extends JpaRepository<PostEntity,Long> {
    /**
     * 비선점 잠금
     */
    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query(value = "UPDATE post SET version = version + 1, like_cnt = like_cnt+1 WHERE post_id=:postId AND version=:version",nativeQuery = true)
    int updatedCount(@Param(value = "postId")Long postId, @Param(value = "version")Long version);

}
