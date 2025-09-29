package com.midas.shootpointer.domain.comment.helper;

import com.midas.shootpointer.domain.comment.entity.Comment;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CommentHelperImpl implements CommentHelper {
	private final CommentValidation commentValidation;
	private final CommentUtil commentUtil;
	@Override
	public void isValidateCommentContent(String content) {
		commentValidation.isValidateCommentContent(content);
	}
	
	@Override
	public Comment save(Comment comment) {
		return commentUtil.save(comment);
	}
}
