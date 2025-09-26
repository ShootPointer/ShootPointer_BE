package com.midas.shootpointer.domain.post.business.query;

import com.midas.shootpointer.domain.post.dto.response.PostListResponse;
import com.midas.shootpointer.domain.post.dto.response.PostResponse;
import com.midas.shootpointer.domain.post.dto.response.PostSort;

public interface PostQueryService {
    PostResponse singleRead(Long decode);

    PostListResponse multiRead(Long postId, int size, String type);

    PostListResponse search(String search,Long postId,int size);

    PostListResponse searchByElastic(String search, PostSort sort);
}
