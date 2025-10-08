package com.midas.shootpointer.domain.post.mapper;

import com.midas.shootpointer.domain.post.entity.PostDocument;
import com.midas.shootpointer.domain.post.entity.PostEntity;

public interface PostElasticSearchMapper {
    PostDocument entityToDoc(PostEntity post);
}
