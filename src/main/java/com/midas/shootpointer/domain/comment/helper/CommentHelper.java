package com.midas.shootpointer.domain.comment.helper;

import com.midas.shootpointer.domain.comment.entity.Comment;
import java.util.List;
import java.util.UUID;

public interface CommentHelper extends CommentValidation, CommentUtil {
	
	List<Comment> findAllByPostIdOrderByCreatedAtDesc(Long postId);
	
	Comment findCommentByCommentId(Long commentId);
	
	void delete(Comment comment);
	
	void validateCommentOwner(Comment comment, UUID memberId);
	
	Comment updateContent(Comment comment, String content);
	
	void validateContentNotBlank(String content);
}
