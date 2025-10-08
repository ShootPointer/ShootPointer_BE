package com.midas.shootpointer.domain.comment.dto.request;

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
	private String content;
}
