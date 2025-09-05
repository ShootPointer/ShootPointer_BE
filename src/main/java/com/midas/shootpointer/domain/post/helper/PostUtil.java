package com.midas.shootpointer.domain.post.helper;

import com.midas.shootpointer.domain.highlight.entity.HighlightEntity;
import com.midas.shootpointer.domain.post.dto.PostRequest;
import com.midas.shootpointer.domain.post.entity.PostEntity;

public interface PostUtil {
    PostEntity findPostByPostId(Long postId);
    PostEntity save(PostEntity postEntity);
    PostEntity update(PostRequest postRequest, PostEntity post, HighlightEntity highlight);
}
