package com.midas.shootpointer.domain.comment.helper;

import com.midas.shootpointer.domain.comment.entity.Comment;
import com.midas.shootpointer.domain.comment.repository.command.CommentCommandRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CommentUtilImpl implements CommentUtil {
	
	private final CommentCommandRepository commentCommandRepository;
	
	@Override
	public Comment save(Comment comment) {
		return commentCommandRepository.save(comment);
	}
}
