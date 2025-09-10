package com.midas.shootpointer.domain.post.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class PostListResponse {
    private Long lastPostId;
    private List<PostResponse> postList;
}
