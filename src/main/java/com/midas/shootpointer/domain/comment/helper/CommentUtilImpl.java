package com.midas.shootpointer.domain.comment.helper;

import com.midas.shootpointer.domain.comment.entity.Comment;
import com.midas.shootpointer.domain.comment.repository.command.CommentCommandRepository;
import com.midas.shootpointer.domain.comment.repository.query.CommentQueryRepository;
import com.midas.shootpointer.global.common.ErrorCode;
import com.midas.shootpointer.global.exception.CustomException;
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
	
	@Override
	public Comment findCommentByCommentId(Long commentId) {
		return commentQueryRepository.findCommentByCommentId(commentId)
			.orElseThrow(() -> new CustomException(ErrorCode.IS_NOT_EXIST_COMMENT));
	}
	
	@Override
	public void delete(Comment comment) {
		comment.delete();
		commentCommandRepository.save(comment);
	}
	
	@Override
	public Comment updateContent(Comment comment, String content) {
		comment.updateContent(content);
		return commentCommandRepository.save(comment);
	}
	
	
}
