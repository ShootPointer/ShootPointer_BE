package com.midas.shootpointer.domain.comment.business;

import com.midas.shootpointer.domain.comment.entity.Comment;
import com.midas.shootpointer.domain.comment.helper.CommentHelper;
import com.midas.shootpointer.domain.post.helper.PostHelper;
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
		
		commentHelper.isValidateCommentContent(comment.getContent()); // 댓글 validation 체크
		
		return commentHelper.save(comment).getCommentId();
	}
	
}
