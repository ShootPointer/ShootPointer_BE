package com.midas.shootpointer.domain.post.repository;

import com.midas.shootpointer.domain.post.entity.PostEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PostQueryRepository extends JpaRepository<PostEntity,Long> {
}
