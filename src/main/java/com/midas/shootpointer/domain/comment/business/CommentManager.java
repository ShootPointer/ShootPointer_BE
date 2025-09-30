package com.midas.shootpointer.domain.comment.business;

import com.midas.shootpointer.domain.comment.entity.Comment;
import com.midas.shootpointer.domain.comment.helper.CommentHelper;
import com.midas.shootpointer.domain.post.helper.PostHelper;
import java.util.List;
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
		
		//* TODO : 첫 번째 코드와 아래 두 번째 코드가 사실상 같은 역할인데 다른 코드로 구현되어있음.
		//* 도메인 역할 분리를 위해 따로 구현할 것인지 OR 그냥 하나로 통합할 것인지
		postHelper.findPostByPostId(comment.getPost().getPostId()); // 게시물이 존재하는지만 확인
		
		commentHelper.validatePostExists(comment.getPost().getPostId()); // 댓글 validation 체크
		
		return commentHelper.save(comment).getCommentId();
	}
	
	@Transactional(readOnly = true)
	public List<Comment> findCommentsByPostId(Long postId) {
		postHelper.findPostByPostId(postId);
		
		return commentHelper.findAllByPostIdOrderByCreatedAtDesc(postId); // 최신순으로 댓글을 모두 조회
	}
}
