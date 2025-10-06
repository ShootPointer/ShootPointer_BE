package com.midas.shootpointer.domain.comment.business.command;

import com.midas.shootpointer.domain.comment.business.CommentManager;
import com.midas.shootpointer.domain.comment.entity.Comment;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CommentCommandServiceImpl implements CommentCommandService {
	
	private final CommentManager commentManager;
	
	@Override
	@Transactional
	public Long create(Comment comment) {
		return commentManager.save(comment);
	}
	
	@Override
	@Transactional
	public void delete(Long commentId, UUID memberId) {
		commentManager.delete(commentId, memberId);
	}
}
