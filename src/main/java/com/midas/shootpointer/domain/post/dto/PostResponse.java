package com.midas.shootpointer.domain.post.dto;

import com.midas.shootpointer.domain.comment.dto.CommentResponse;
import com.midas.shootpointer.domain.post.entity.HashTag;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@AllArgsConstructor
@Getter
@NoArgsConstructor
@Builder
/**
 * 게시물 조회 dto
 */
public class PostResponse {
    //게시물 Id
    Long postId;

    //게시물 제목
    String title;

    //게시물 내용
    String content;

    //동영상 url
    String highlightUrl;

    //게시 시간
    LocalDateTime createdAt;

    //수정 시간
    LocalDateTime modifiedAt;

    //해시 태그
    HashTag hashTag;
}
