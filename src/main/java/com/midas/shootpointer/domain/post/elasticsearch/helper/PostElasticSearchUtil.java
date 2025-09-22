package com.midas.shootpointer.domain.post.elasticsearch.helper;

import com.midas.shootpointer.domain.post.dto.response.PostResponse;
import com.midas.shootpointer.domain.post.entity.PostEntity;

import java.util.List;

public interface PostElasticSearchUtil {
    Long createPostDocument(PostEntity post);
    List<PostResponse> getPostByTitleOrContentByElasticSearch(String search, Long lastPostId, int size);
}
