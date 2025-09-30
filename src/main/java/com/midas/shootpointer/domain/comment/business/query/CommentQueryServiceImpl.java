package com.midas.shootpointer.domain.comment.business.query;

import com.midas.shootpointer.domain.comment.business.CommentManager;
import com.midas.shootpointer.domain.comment.entity.Comment;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CommentQueryServiceImpl implements CommentQueryService {
	
	private final CommentManager commentManager;
	
	@Override
	@Transactional(readOnly = true)
	public List<Comment> getCommentsByPostId(Long postId) {
		return commentManager.findCommentByPostId(postId);
	}
}
