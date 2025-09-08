package com.midas.shootpointer.domain.post.helper;

import com.midas.shootpointer.domain.highlight.entity.HighlightEntity;
import com.midas.shootpointer.domain.post.dto.PostRequest;
import com.midas.shootpointer.domain.post.dto.PostResponse;
import com.midas.shootpointer.domain.post.entity.PostEntity;

public interface PostUtil {
    PostEntity findPostByPostId(Long postId);
    PostEntity save(PostEntity postEntity);
    PostEntity update(PostEntity newPost, PostEntity existedPost, HighlightEntity highlight);
    PostEntity findByPostByPostIdWithPessimisticLock(Long postId);
    PostResponse singleRead(Long postId);
}
