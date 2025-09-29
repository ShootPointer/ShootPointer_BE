package com.midas.shootpointer.domain.comment.dto.request;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CommentRequestDto {
	
	@NotNull(message = "게시물 ID는 필수입니다.")
	private Long postId;
	
	@NotBlank(message = "댓글 내용은 공백이 허용되지 않습니다.")
	@Max(value = 500, message = "댓글 내용은 500자 이하만 입력 가능합니다.")
	private String content;
}
