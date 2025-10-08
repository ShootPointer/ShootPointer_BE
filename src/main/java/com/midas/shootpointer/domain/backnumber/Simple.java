package com.midas.shootpointer.domain.backnumber;

import com.midas.shootpointer.domain.post.entity.PostEntity;
import org.springframework.data.jpa.repository.support.SimpleJpaRepository;

public class Simple {
    private SimpleJpaRepository<PostEntity,Long> entityLongSimpleJpaRepository;
}
