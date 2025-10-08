package com.midas.shootpointer.domain.comment.helper;

import com.midas.shootpointer.domain.comment.entity.Comment;
import java.util.UUID;

public interface CommentValidation {
	
	void validatePostExists(Long postId);
	
	void validateCommentOwner(Comment comment, UUID memberId);
	
	void validateContentNotBlank(String content);
}
