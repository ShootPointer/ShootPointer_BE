package com.midas.shootpointer.domain.post.repository;

import com.midas.shootpointer.domain.post.entity.PostEntity;
import jakarta.persistence.LockModeType;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
@Repository
public interface PostQueryRepository extends JpaRepository<PostEntity,Long>{
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
    @Query(value = "SELECT * FROM post as p WHERE p.post_id < :lastPostId ORDER BY p.post_id DESC LIMIT :size ",nativeQuery = true)
    List<PostEntity> getLatestPostListBySliceAndNoOffset (@Param(value = "lastPostId")Long lastPostId,@Param(value = "size")int size);

    /**
     * 좋아요순 게시물 다건 조회
     * 좋아요가 같은 경우는 최신 순 봔환.
     */
    @Query(value = "SELECT * FROM post as p " +
                   "WHERE p.like_cnt < :likeCnt " +
                   "ORDER BY p.like_cnt DESC, p.post_id DESC " +
                   "LIMIT :size",
            nativeQuery = true
    )
    List<PostEntity> getPopularPostListBySliceAndNoOffset(@Param(value = "size")int size,
                                                          @Param(value = "likeCnt")Long likeCnt
    );

    /**
     * 1. 제목 + 내용 게시물 조회 - NoOffset+slice 방식
     * 2. 조회된 게시물 최신순 정렬 반환.
     */
    @Query(value = """
        SELECT *
        FROM post p
        WHERE (p.title LIKE CONCAT('%', :search, '%')
            OR p.content LIKE CONCAT('%', :search, '%'))
          AND p.post_id < :lastPostId
        ORDER BY p.post_id DESC
        LIMIT :size
        """,
            nativeQuery = true)
    List<PostEntity> getPostEntitiesByPostTitleOrPostContentOrderByCreatedAtDesc(
            @Param("search") String search,
            @Param("size") int size,
            @Param("lastPostId") Long postId
    );


    @Query(value = "SELECT p FROM PostEntity p JOIN FETCH p.member m JOIN FETCH p.highlight h")
    List<PostEntity> findAllWithMemberAndHighlight();
    
    /**
     * 회원이 작성한 모든 게시물 ID 조회
     */
    @Query(value = "SELECT p.post_id FROM post AS p " +
        "WHERE p.member_id = :memberId " +
        "ORDER BY p.post_id DESC",
        nativeQuery = true)
    List<Long> findPostIdsByMemberId(@Param(value = "memberId") UUID memberId);
    
    @Query(value = "SELECT p FROM PostEntity p " +
        "JOIN FETCH p.member m " +
        "JOIN FETCH p.highlight h " +
        "WHERE p.postId IN :postIds " +
        "ORDER BY p.postId DESC")
    List<PostEntity> findPostsByPostIds(@Param(value = "postIds") List<Long> postIds);
}
