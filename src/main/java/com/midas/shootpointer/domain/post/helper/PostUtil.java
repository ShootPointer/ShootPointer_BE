package com.midas.shootpointer.domain.post.helper;

import com.midas.shootpointer.domain.post.entity.PostEntity;

public interface PostUtil {
    PostEntity findPostByPostId(Long postId);
    PostEntity save(PostEntity postEntity);
    PostEntity findByPostByPostIdWithPessimisticLock(Long postId);
}
