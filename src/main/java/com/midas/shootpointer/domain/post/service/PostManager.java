package com.midas.shootpointer.domain.post.service;

import com.midas.shootpointer.domain.member.entity.Member;
import com.midas.shootpointer.domain.post.entity.PostEntity;
import com.midas.shootpointer.domain.post.repository.PostCommandRepository;
import com.midas.shootpointer.domain.post.repository.PostQueryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PostManager {
    private final PostCommandRepository postCommandRepository;
    private final PostQueryRepository postQueryRepository;

    public Long save(Member member, PostEntity postEntity){
       return postCommandRepository.save(postEntity).getPostId();
    }
}
