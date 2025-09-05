package com.midas.shootpointer.domain.post.helper;

import com.midas.shootpointer.domain.post.entity.PostEntity;
import com.midas.shootpointer.domain.post.repository.PostCommandRepository;
import com.midas.shootpointer.domain.post.repository.PostQueryRepository;
import com.midas.shootpointer.global.common.ErrorCode;
import com.midas.shootpointer.global.exception.CustomException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class PostUtilImpl implements PostUtil{
    private final PostQueryRepository postQueryRepository;
    private final PostCommandRepository postCommandRepository;
    @Override
    public PostEntity findPostByPostId(Long postId) {
        return postQueryRepository.findByPostId(postId)
                .orElseThrow(()->new CustomException(ErrorCode.IS_NOT_EXIST_POST));
    }

    @Override
    public PostEntity save(PostEntity postEntity) {
        return postCommandRepository.save(postEntity);
    }

    @Override
    @Transactional
    public PostEntity findByPostByPostIdWithPessimisticLock(Long postId) {
        return postQueryRepository.findByPostIdWithPessimisticLock(postId);
    }
}
