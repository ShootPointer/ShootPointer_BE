package com.midas.shootpointer.domain.post.mapper;

import com.midas.shootpointer.domain.post.dto.PostRequest;
import com.midas.shootpointer.domain.post.dto.PostResponse;
import com.midas.shootpointer.domain.post.entity.PostEntity;
import org.springframework.stereotype.Component;

@Component
public class PostMapperImpl implements PostMapper{
    @Override
    public PostEntity dtoToEntity(PostRequest postRequest) {
        return null;
    }

    @Override
    public PostResponse entityToDto(PostEntity postEntity) {
        return null;
    }
}
