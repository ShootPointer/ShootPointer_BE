package com.midas.shootpointer.domain.post.repository;

import com.midas.shootpointer.domain.post.entity.PostEntity;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
@Repository
public interface PostQueryRepository extends JpaRepository<PostEntity,Long> {
    Optional<PostEntity> findByPostId(Long postId);
    /**
     * 선점 잠금
     */
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query(value = "SELECT p FROM PostEntity AS p WHERE p.postId=:postId")
    PostEntity findByPostIdWithPessimisticLock(@Param(value = "postId")Long postId);

    /**
     * 최신순 게시물 다건 조회
     */
    @Query(value = "SELECT p FROM post as p WHERE post_id < :lastPostId ORDER BY post_id DESC LIMIT :size ",nativeQuery = true)
    List<PostEntity> getLatestPostListBySliceAndNoOffset (@Param(value = "lastPostId")Long lastPostId,@Param(value = "size")int size);

    /**
     * 좋아요순 게시물 다건 조회
     * 좋아요가 같은 경우는 최신 순 봔환.
     */
    @Query(value = "SELECT p FROM post as p " +
                   "WHERE (like_cnt < :likeCnt) OR " +
                   "(like_cnt = :likeCnt AND post_id < :postId) " +
                   "ORDER BY like_cnt DESC " +
                   "LIMIT :size",
            nativeQuery = true
    )
    List<PostEntity> getPopularPostListBySliceAndNoOffset(@Param(value = "lastPostId")Long lastPostId,
                                                          @Param(value = "size")int size,
                                                          @Param(value = "likeCnt")Long likeCnt
    );
}
