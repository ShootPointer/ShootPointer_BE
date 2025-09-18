package com.midas.shootpointer.domain.post.repository;

import com.midas.shootpointer.domain.post.entity.PostEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PostCommandRepository extends JpaRepository<PostEntity,Long> {
}
