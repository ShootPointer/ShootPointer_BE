package com.midas.shootpointer.domain.post.repository;

import com.midas.shootpointer.domain.member.entity.Member;
import com.midas.shootpointer.domain.post.dto.PostResponse;
import com.midas.shootpointer.domain.post.entity.PostEntity;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

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
     * 게시물 단 건 조회
     */
    @Query(value = "SELECT new com.midas.shootpointer.domain.post.dto.PostResponse(" +
                   "p.postId," +
                   "p.title," +
                   "p.content," +
                   "p.highlight.highlightURL," +
                   "p.likeCnt,p.createdAt," +
                   "p.modifiedAt," +
                   "p.hashTag" +
                   ") " +
                   "FROM PostEntity p " +
                   "WHERE p.postId = :postId"
    )
    Optional<PostResponse> findPostResponseDtoByPostId(@Param(value = "postId") Long postId);
}
