package com.midas.shootpointer.domain.post.dto;

import com.midas.shootpointer.domain.comment.dto.CommentResponse;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@AllArgsConstructor
@Getter
@NoArgsConstructor
/**
 * 게시물 조회 dto
 */
public class PostResponse {
    //게시자 이름
    String memberName;

    //게시물 제목
    String title;

    //게시물 내용
    String content;

    //동영상 url
    String highlightUrl;

    //좋아요 개수
    Integer likeCnt;

    //댓글 개수
    Integer commentCnt;

    //댓글 리스트
    List<CommentResponse> comments;

    //게시 시간
    LocalDateTime createdAt;

    //수정 시간
    LocalDateTime modifiedAt;
}
