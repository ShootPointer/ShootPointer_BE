package com.midas.shootpointer.domain.comment.helper;

import static org.assertj.core.api.Assertions.assertThatNoException;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.*;

import com.midas.shootpointer.global.common.ErrorCode;
import com.midas.shootpointer.global.exception.CustomException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
@DisplayName("CommentValidation 테스트")
class CommentValidationImplTest {
	
	@InjectMocks
	private CommentValidationImpl commentValidation;
	
	@Test
	@DisplayName("유효한 댓글 내용 검증 성공")
	void isValidateCommentContent_ValidContent_Success() {
		// given
		String validContent = "유효한 댓글 내용입니다.";
		
		// when-then
		assertThatNoException().isThrownBy(() ->
			commentValidation.isValidateCommentContent(validContent));
	}
	
	@ParameterizedTest
	@NullAndEmptySource
	@ValueSource(strings = {"   ", "\t", "\n", "  \t  \n  "})
	@DisplayName("null, 빈 문자열, 공백만 있는 댓글 내용은 유효하지 않음")
	void isValidateCommentContent_InvalidContent_ThrowException(String invalidContent) {
		// when-then
		assertThatThrownBy(() -> commentValidation.isValidateCommentContent(invalidContent))
			.isInstanceOf(CustomException.class)
			.hasFieldOrPropertyWithValue("errorCode", ErrorCode.INVALID_COMMENT_CONTENT);
	}
}