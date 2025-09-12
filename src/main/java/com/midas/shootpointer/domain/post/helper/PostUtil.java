package com.midas.shootpointer.domain.post.helper;

import com.midas.shootpointer.domain.highlight.entity.HighlightEntity;
import com.midas.shootpointer.domain.post.business.PostOrderType;
import com.midas.shootpointer.domain.post.entity.PostEntity;

import java.util.List;

public interface PostUtil {
    PostEntity findPostByPostId(Long postId);
    PostEntity save(PostEntity postEntity);
    PostEntity update(PostEntity newPost, PostEntity oldPost, HighlightEntity highlight);
    PostEntity findByPostByPostIdWithPessimisticLock(Long postId);
    List<PostEntity> getLatestPostListBySliceAndNoOffset(Long postId,int size);
    List<PostEntity> getPopularPostListBySliceAndNoOffset(Long postId,int size);
    PostOrderType isValidAndGetPostOrderType(String type);
}
