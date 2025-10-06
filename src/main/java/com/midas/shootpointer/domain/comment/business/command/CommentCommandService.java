package com.midas.shootpointer.domain.comment.business.command;

import com.midas.shootpointer.domain.comment.entity.Comment;
import java.util.UUID;

public interface CommentCommandService {
	
	Long create(Comment comment);
	
	void delete(Long commentId, UUID memberId);
}
