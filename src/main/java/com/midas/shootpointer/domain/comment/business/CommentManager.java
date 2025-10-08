package com.midas.shootpointer.domain.comment.business;

import com.midas.shootpointer.domain.comment.entity.Comment;
import com.midas.shootpointer.domain.comment.helper.CommentHelper;
import com.midas.shootpointer.domain.post.helper.PostHelper;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class CommentManager {
	
	private final CommentHelper commentHelper;
	private final PostHelper postHelper;
	
	@Transactional
	public Long save(Comment comment) {
		postHelper.findPostByPostId(comment.getPost().getPostId()); // 게시물이 존재하는지만 확인
		
		return commentHelper.save(comment).getCommentId();
	}
	
	@Transactional(readOnly = true)
	public List<Comment> findCommentsByPostId(Long postId) {
		postHelper.findPostByPostId(postId);
		
		return commentHelper.findAllByPostIdOrderByCreatedAtDesc(postId); // 최신순으로 댓글을 모두 조회
	}
	
	@Transactional
	public void delete(Long commentId, UUID memberId) {
		Comment comment = commentHelper.findCommentByCommentId(commentId);
		commentHelper.validateCommentOwner(comment, memberId);
		commentHelper.delete(comment);
	}
	
	@Transactional
	public Comment update(Long commentId, String content, UUID memberId) {
		Comment comment = commentHelper.findCommentByCommentId(commentId);
		commentHelper.validateCommentOwner(comment, memberId);
		commentHelper.validateContentNotBlank(content);
		commentHelper.updateContent(comment, content);
		return comment;
	}
}
