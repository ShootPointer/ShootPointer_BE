package com.midas.shootpointer.domain.comment.business.command;

import com.midas.shootpointer.domain.comment.entity.Comment;

public interface CommentCommandService {
	
	Long create(Comment comment);
}
