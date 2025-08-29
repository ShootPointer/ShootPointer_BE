package com.midas.shootpointer.domain.post.mapper;

import com.midas.shootpointer.domain.member.entity.Member;
import com.midas.shootpointer.domain.post.dto.PostRequest;
import com.midas.shootpointer.domain.post.dto.PostResponse;
import com.midas.shootpointer.domain.post.entity.PostEntity;
import org.springframework.stereotype.Component;

@Component
public class PostMapperImpl implements PostMapper{
    @Override
    public PostEntity dtoToEntity(PostRequest postRequest, Member member) {
        return PostEntity.builder()
                .content(postRequest.getContent())
                .title(postRequest.getTitle())
                .hashTag(postRequest.getHashTag())
                .member(member)
                .build();
    }
}
