package com.midas.shootpointer.domain.post.helper.simple;

import com.midas.shootpointer.domain.highlight.entity.HighlightEntity;
import com.midas.shootpointer.domain.post.business.PostOrderType;
import com.midas.shootpointer.domain.post.entity.PostEntity;

import java.util.List;
import java.util.UUID;

public interface PostUtil {
    PostEntity findPostByPostId(Long postId);
    PostEntity save(PostEntity postEntity);
    PostEntity update(PostEntity newPost, PostEntity oldPost, HighlightEntity highlight);
    PostEntity findByPostByPostIdWithPessimisticLock(Long postId);
    List<PostEntity> getLatestPostListBySliceAndNoOffset(Long postId,int size);
    List<PostEntity> getPopularPostListBySliceAndNoOffset(Long postId,int size);
    PostOrderType isValidAndGetPostOrderType(String type);
    List<PostEntity> getPostEntitiesByPostTitleOrPostContent(String search,Long postId,int size);
    List<Long> findPostIdsByMemberId(UUID memberId);
    List<PostEntity> findPostsByPostIds(List<Long> postIds);
    List<PostEntity> getMyLikedPost(UUID memberId,Long lastPostId,int size);
}
