package com.midas.shootpointer.domain.post.business.query;

import com.midas.shootpointer.domain.post.dto.PostResponse;

public interface PostQueryService {
    PostResponse singleRead(Long decode);

    PostResponse multiRead(Long postId, int size, String type);
}
