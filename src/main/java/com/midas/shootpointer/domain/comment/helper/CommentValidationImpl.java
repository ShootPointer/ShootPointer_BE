package com.midas.shootpointer.domain.comment.helper;

import com.midas.shootpointer.global.common.ErrorCode;
import com.midas.shootpointer.global.exception.CustomException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CommentValidationImpl implements CommentValidation {
	
	@Override
	public void isValidateCommentContent(String content) {
		if (content == null || content.trim().isEmpty()) { // 유효성 검사 시 댓글이 null 이거나 trim으로 공백 제거 후 비어있으면 예외 던지기
			throw new CustomException(ErrorCode.INVALID_COMMENT_CONTENT);
		}
	}
}
