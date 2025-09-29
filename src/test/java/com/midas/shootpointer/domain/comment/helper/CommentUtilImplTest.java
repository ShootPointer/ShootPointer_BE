package com.midas.shootpointer.domain.comment.helper;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.BDDMockito.willThrow;

import com.midas.shootpointer.domain.comment.entity.Comment;
import com.midas.shootpointer.domain.comment.repository.command.CommentCommandRepository;
import com.midas.shootpointer.domain.member.entity.Member;
import com.midas.shootpointer.domain.post.entity.PostEntity;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataAccessException;

@ExtendWith(MockitoExtension.class)
@DisplayName("CommentUtil 테스트")
class CommentUtilImplTest {
	
	@Mock
	private CommentCommandRepository commentCommandRepository;
	
	@InjectMocks
	private CommentUtilImpl commentUtil;
	
	@Test
	@DisplayName("댓글 저장 성공")
	void save_Success() {
		// given
		Comment comment = createComment();
		Comment savedComment = Comment.builder()
			.commentId(1L)
			.content(comment.getContent())
			.member(comment.getMember())
			.post(comment.getPost())
			.build();
		
		given(commentCommandRepository.save(comment)).willReturn(savedComment);
		
		// when
		Comment result = commentUtil.save(comment);
		
		// then
		assertThat(result).isEqualTo(savedComment);
		assertThat(result.getCommentId()).isEqualTo(1L);
		assertThat(result.getContent()).isEqualTo("테스트 댓글입니다.");
		then(commentCommandRepository).should().save(comment);
	}
	
	private Comment createComment() {
		Member member = Member.builder()
			.memberId(java.util.UUID.randomUUID())
			.email("test@test.com")
			.username("testUser")
			.build();
		
		PostEntity post = PostEntity.builder()
			.postId(1L)
			.build();
		
		return Comment.builder()
			.content("테스트 댓글입니다.")
			.member(member)
			.post(post)
			.build();
	}
}