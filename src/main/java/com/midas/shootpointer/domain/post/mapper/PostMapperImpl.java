package com.midas.shootpointer.domain.post.mapper;

import com.midas.shootpointer.domain.member.entity.Member;
import com.midas.shootpointer.domain.post.dto.request.PostRequest;
import com.midas.shootpointer.domain.post.dto.response.PostListResponse;
import com.midas.shootpointer.domain.post.dto.response.PostResponse;
import com.midas.shootpointer.domain.post.entity.PostDocument;
import com.midas.shootpointer.domain.post.entity.PostEntity;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;

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

    @Override
    public PostResponse entityToDto(PostEntity post) {
        return PostResponse.builder()
                .memberName(post.getMember().getUsername())
                .hashTag(post.getHashTag().getName())
                .postId(post.getPostId())
                .title(post.getTitle())
                .modifiedAt(post.getModifiedAt())
                .highlightUrl(post.getHighlight().getHighlightURL())
                .createdAt(post.getCreatedAt())
                .content(post.getContent())
                .likeCnt(post.getLikeCnt())
                .build();
    }

    @Override
    public PostListResponse entityToDto(List<PostEntity> postEntityList) {
        //값이 없는 경우
        if (postEntityList.isEmpty()){
            return PostListResponse.of(922337203685477580L, Collections.emptyList());
        }

        //게시물 응답 Dto 리스트 변환
        List<PostResponse> postResponses=postEntityList.stream()
                .map(this::entityToDto)
                .toList();

        int size=postResponses.size();

        return PostListResponse.of(postResponses.get(size-1).getPostId(),postResponses);
    }

    @Profile("es")
    @Override
    public PostResponse documentToResponse(PostDocument document) {
         return PostResponse.builder()
                .title(document.getTitle())
                .content(document.getContent())
                .createdAt(document.getCreatedAt())
                .memberName(document.getMemberName())
                .hashTag(document.getHashTag())
                .highlightUrl(document.getHighlightUrl())
                .likeCnt(document.getLikeCnt())
                .modifiedAt(document.getModifiedAt())
                .postId(document.getPostId())
                .build();
    }
}
