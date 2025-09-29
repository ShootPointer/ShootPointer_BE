package com.midas.shootpointer.domain.comment.helper;

import com.midas.shootpointer.domain.post.helper.PostHelper;
import com.midas.shootpointer.global.common.ErrorCode;
import com.midas.shootpointer.global.exception.CustomException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CommentValidationImpl implements CommentValidation {
	
	private final PostHelper postHelper;
	
	@Override
	public void validatePostExists(Long postId) {
		try {
			postHelper.findPostByPostId(postId);
		} catch (Exception e) {
			throw new CustomException(ErrorCode.IS_NOT_EXIST_POST);
		}
	}
}
