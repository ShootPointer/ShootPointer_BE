package com.midas.shootpointer.domain.post.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PostListResponse {
    private Long lastPostId;
    private List<PostResponse> postList;
    private PostSort sort;

    @Builder
    public static PostListResponse of(Long lastPostId,List<PostResponse> postList){
        return new PostListResponse(lastPostId,postList,null);
    }

    @Builder
    public static PostListResponse withSort(Long lastPostId,List<PostResponse> postList,PostSort sort){
        return new PostListResponse(lastPostId,postList,sort);
    }
}
