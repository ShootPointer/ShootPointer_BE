package com.midas.shootpointer.domain.post.dto;

import com.midas.shootpointer.domain.post.entity.HashTag;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@Getter
@NoArgsConstructor
/**
 * 게시물 등록 dto
 */
public class PostRequest {

    //하이라이트 영상 ID(UUID)
    @NotBlank(message = "하이라이트 영상 id는 공백일 수 없습니다.")
    @Pattern(regexp = "^[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{12}$",
            message = "하이라이트 영상 id가 유효하지 않습니다."
    )
    private String highlightId;

    //제목
    @NotBlank(message = "제목은 공백일 수 없습니다.")
    @Max(value = 20,message = "제목은 20자 이하만 입력가능합니다.")
    private String title;

    //내용
    @NotBlank(message = "내용은 공백일 수 없습니다.")
    @Max(value = 1000,message = "내용은 1000자 이하만 입력가능합니다.")
    private String content;

    //해시 태그
    private HashTag hashTag;

    public static PostRequest of(String highlightId,
                                 String title,
                                 String content,
                                 HashTag hashTag
    ){
        return new PostRequest(highlightId,title,content,hashTag);
    }

}
