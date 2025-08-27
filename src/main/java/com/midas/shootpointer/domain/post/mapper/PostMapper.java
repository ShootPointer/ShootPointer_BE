package com.midas.shootpointer.domain.post.mapper;

import com.midas.shootpointer.domain.post.dto.PostRequest;
import com.midas.shootpointer.domain.post.dto.PostResponse;
import com.midas.shootpointer.domain.post.entity.PostEntity;

public interface PostMapper {
    PostEntity dtoToEntity(PostRequest postRequest);
    PostResponse entityToDto(PostEntity postEntity);
}
