package com.midas.shootpointer.domain.post.repository;

import com.midas.shootpointer.domain.member.entity.Member;
import com.midas.shootpointer.domain.post.entity.PostEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface PostQueryRepository extends JpaRepository<PostEntity,Long> {
    Optional<PostEntity> findByPostId(Long postId);
}
