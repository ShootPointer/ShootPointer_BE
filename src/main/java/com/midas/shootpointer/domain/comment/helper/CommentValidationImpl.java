package com.midas.shootpointer.domain.comment.helper;

import com.midas.shootpointer.domain.comment.entity.Comment;
import com.midas.shootpointer.domain.post.helper.PostHelper;
import com.midas.shootpointer.global.common.ErrorCode;
import com.midas.shootpointer.global.exception.CustomException;
import java.util.UUID;
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
	
	@Override
	public void validateCommentOwner(Comment comment, UUID memberId) {
		if (!comment.getMember().getMemberId().equals(memberId)) { // 댓글 작성자가 실제 로그인한 사용자가 아닌 경우
			throw new CustomException(ErrorCode.FORBIDDEN_COMMENT_ACCESS);
		}
	}
	
}
