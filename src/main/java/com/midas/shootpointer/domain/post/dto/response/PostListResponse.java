package com.midas.shootpointer.domain.post.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
public class PostListResponse {
    private Long lastPostId;
    private List<PostResponse> postList;

    @Builder
    public static PostListResponse of(Long lastPostId,List<PostResponse> postList){
        return new PostListResponse(lastPostId,postList);
    }
}
