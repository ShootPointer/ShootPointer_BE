package com.midas.shootpointer.domain.comment.helper;

import com.midas.shootpointer.domain.comment.entity.Comment;
import java.util.List;

public interface CommentHelper extends CommentValidation, CommentUtil {
	
	List<Comment> findAllByPostIdOrderByCreatedAtDesc(Long postId);
}
