package com.midas.shootpointer.domain.post.elasticsearch.mapper;

import com.midas.shootpointer.domain.post.dto.response.PostResponse;
import com.midas.shootpointer.domain.post.elasticsearch.PostDocument;
import com.midas.shootpointer.domain.post.entity.PostEntity;

public interface PostElasticSearchMapper {
    PostResponse docToResponse(PostDocument doc);
    PostDocument entityToDoc(PostEntity post);
}
