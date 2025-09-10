package com.midas.shootpointer.domain.post.helper;

import com.midas.shootpointer.domain.highlight.entity.HighlightEntity;
import com.midas.shootpointer.domain.post.entity.PostEntity;

public interface PostUtil {
    PostEntity findPostByPostId(Long postId);
    PostEntity save(PostEntity postEntity);
    PostEntity update(PostEntity newPost, PostEntity oldPost, HighlightEntity highlight);
    PostEntity findByPostByPostIdWithPessimisticLock(Long postId);
}
