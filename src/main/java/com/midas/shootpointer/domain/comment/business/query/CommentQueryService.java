package com.midas.shootpointer.domain.comment.business.query;

import com.midas.shootpointer.domain.comment.entity.Comment;
import java.util.List;

public interface CommentQueryService {
	
	List<Comment> getCommentsByPostId(Long postId);
}
