package com.midas.shootpointer.domain.comment.business.command;

import com.midas.shootpointer.domain.comment.business.CommentManager;
import com.midas.shootpointer.domain.comment.entity.Comment;
import com.midas.shootpointer.domain.comment.helper.CommentHelper;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class CommentCommandServiceImpl implements CommentCommandService {
	
	private final CommentManager commentManager;
	
	@Override
	public Long create(Comment comment) {
		return commentManager.save(comment);
	}
	
	@Override
	public void delete(Long commentId, UUID memberId) {
		commentManager.delete(commentId, memberId);
	}
	
	@Override
	public Comment update(Long commentId, String content, UUID memberId) {
		return commentManager.update(commentId, content, memberId);
	}
}
