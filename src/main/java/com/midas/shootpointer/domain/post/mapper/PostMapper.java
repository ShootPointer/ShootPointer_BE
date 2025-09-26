package com.midas.shootpointer.domain.post.mapper;

import com.midas.shootpointer.domain.member.entity.Member;
import com.midas.shootpointer.domain.post.dto.request.PostRequest;
import com.midas.shootpointer.domain.post.dto.response.PostListResponse;
import com.midas.shootpointer.domain.post.dto.response.PostResponse;
import com.midas.shootpointer.domain.post.entity.PostDocument;
import com.midas.shootpointer.domain.post.entity.PostEntity;

import java.util.List;

public interface PostMapper {
    PostEntity dtoToEntity(PostRequest postRequest, Member member);
    PostResponse entityToDto(PostEntity post);
    PostListResponse entityToDto(List<PostEntity> postEntityList);
    PostResponse documentToEResponse(PostDocument document);
}
