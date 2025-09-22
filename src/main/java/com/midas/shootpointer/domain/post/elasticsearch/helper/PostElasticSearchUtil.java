package com.midas.shootpointer.domain.post.elasticsearch.helper;

import com.midas.shootpointer.domain.post.entity.PostEntity;

public interface PostElasticSearchUtil {
    Long createPostDocument(PostEntity post);
}
