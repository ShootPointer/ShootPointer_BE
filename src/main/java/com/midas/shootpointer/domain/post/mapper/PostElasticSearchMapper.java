package com.midas.shootpointer.domain.post.mapper;

import com.midas.shootpointer.domain.post.dto.response.PostResponse;
import com.midas.shootpointer.domain.post.entity.PostDocument;
import com.midas.shootpointer.domain.post.entity.PostEntity;

public interface PostElasticSearchMapper {
    PostResponse docToResponse(PostDocument doc);
    PostDocument entityToDoc(PostEntity post);
}
