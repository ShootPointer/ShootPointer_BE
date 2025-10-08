package com.midas.shootpointer.domain.post.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
@Builder
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_EMPTY)
/**
 * 게시물 조회 dto
 */
public class PostResponse {
    //게시물 Id
    private Long postId;

    //게시물 제목
    private String title;

    //게시물 내용
    private String content;

    //동영상 url
    private String highlightUrl;

    //좋아요 개수
    private Long likeCnt;

    //게시 시간
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm", timezone = "Asia/Seoul")
    private LocalDateTime createdAt;

    //수정 시간
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm", timezone = "Asia/Seoul")
    private LocalDateTime modifiedAt;

    //해시 태그
    private String hashTag;

    //작성자 이름
    private String memberName;

}
