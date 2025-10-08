package com.midas.shootpointer.domain.comment.helper;

import com.midas.shootpointer.domain.comment.entity.Comment;
import java.util.List;

public interface CommentUtil {
	
	Comment save(Comment comment);
	
	List<Comment> findAllByPostIdOrderByCreatedAtDesc(Long postId);
	
	Comment findCommentByCommentId(Long commentId);
	
	void delete(Comment comment);
	
	void updateContent(Comment comment, String content);
}
