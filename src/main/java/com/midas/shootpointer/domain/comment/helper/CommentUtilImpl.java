package com.midas.shootpointer.domain.comment.helper;

import com.midas.shootpointer.domain.comment.entity.Comment;
import com.midas.shootpointer.domain.comment.repository.command.CommentCommandRepository;
import com.midas.shootpointer.domain.comment.repository.query.CommentQueryRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CommentUtilImpl implements CommentUtil {
	
	private final CommentCommandRepository commentCommandRepository;
	private final CommentQueryRepository commentQueryRepository;
	
	@Override
	public Comment save(Comment comment) {
		return commentCommandRepository.save(comment);
	}
	
	@Override
	public List<Comment> findAllByPostIdOrderByCreatedAtDesc(Long postId) {
		return commentQueryRepository.findAllByPostIdOrderByCreatedAtDesc(postId);
	}
}
