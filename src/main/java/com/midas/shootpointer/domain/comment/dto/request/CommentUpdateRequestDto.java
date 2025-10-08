package com.midas.shootpointer.domain.comment.dto.request;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CommentUpdateRequestDto {
	
	@NotBlank(message = "댓글 내용은 공백이 허용되지 않습니다.")
	@Max(value = 500, message = "댓글은 1자 이상 500자 이하로 작성해주세요.")
	private String content;
}
