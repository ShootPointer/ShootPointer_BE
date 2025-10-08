package com.midas.shootpointer.domain.comment.helper;

import com.midas.shootpointer.domain.comment.entity.Comment;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CommentHelperImpl implements CommentHelper {
	private final CommentValidation commentValidation;
	private final CommentUtil commentUtil;
	
	@Override
	public void validatePostExists(Long postId) {
		commentValidation.validatePostExists(postId);
	}
	
	@Override
	public Comment save(Comment comment) {
		return commentUtil.save(comment);
	}
	
	@Override
	public List<Comment> findAllByPostIdOrderByCreatedAtDesc(Long postId) {
		return commentUtil.findAllByPostIdOrderByCreatedAtDesc(postId);
	}
	
	@Override
	public Comment findCommentByCommentId(Long commentId) {
		return commentUtil.findCommentByCommentId(commentId);
	}
	
	@Override
	public void delete(Comment comment) {
		commentUtil.delete(comment);
	}
	
	@Override
	public void validateCommentOwner(Comment comment, UUID memberId) {
		commentValidation.validateCommentOwner(comment, memberId);
	}
	
	@Override
	public void updateContent(Comment comment, String content) {
		commentUtil.updateContent(comment, content);
	}
	
	@Override
	public void validateContentNotBlank(String content) {
		commentValidation.validateContentNotBlank(content);
	}
}
