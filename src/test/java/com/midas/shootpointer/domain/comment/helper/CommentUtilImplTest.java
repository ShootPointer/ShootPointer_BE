package com.midas.shootpointer.domain.comment.helper;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.BDDMockito.willThrow;

import com.midas.shootpointer.domain.comment.entity.Comment;
import com.midas.shootpointer.domain.comment.repository.command.CommentCommandRepository;
import com.midas.shootpointer.domain.comment.repository.query.CommentQueryRepository;
import com.midas.shootpointer.domain.member.entity.Member;
import com.midas.shootpointer.domain.post.entity.PostEntity;
import java.util.Arrays;
import java.util.List;
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
	
	@Mock
	private CommentQueryRepository commentQueryRepository;
	
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
	
	@Test
	@DisplayName("게시물 ID로 댓글 목록 조회 성공 - 최신순 정렬")
	void findAllByPostIdOrderByCreatedAtDesc_Success() {
		// given
		Long postId = 1L;
		PostEntity post = PostEntity.builder()
			.postId(postId)
			.build();
		
		List<Comment> comments = Arrays.asList(
			createCommentWithId(3L, "최신 댓글", post),
			createCommentWithId(2L, "중간 댓글", post),
			createCommentWithId(1L, "오래된 댓글", post)
		);
		
		given(commentQueryRepository.findAllByPostIdOrderByCreatedAtDesc(postId))
			.willReturn(comments);
		
		// when
		List<Comment> result = commentUtil.findAllByPostIdOrderByCreatedAtDesc(postId);
		
		// then
		assertThat(result).hasSize(3);
		assertThat(result.get(0).getCommentId()).isEqualTo(3L);
		assertThat(result.get(0).getContent()).isEqualTo("최신 댓글");
		assertThat(result.get(1).getCommentId()).isEqualTo(2L);
		assertThat(result.get(2).getCommentId()).isEqualTo(1L);
		then(commentQueryRepository).should().findAllByPostIdOrderByCreatedAtDesc(postId);
	}
	
	@Test
	@DisplayName("댓글 목록 조회 성공 - 빈 리스트")
	void findAllByPostIdOrderByCreatedAtDesc_Success_EmptyList() {
		// given
		Long postId = 999L;
		given(commentQueryRepository.findAllByPostIdOrderByCreatedAtDesc(postId))
			.willReturn(List.of());
		
		// when
		List<Comment> result = commentUtil.findAllByPostIdOrderByCreatedAtDesc(postId);
		
		// then
		assertThat(result).isEmpty();
		then(commentQueryRepository).should().findAllByPostIdOrderByCreatedAtDesc(postId);
	}
	
	private Comment createComment() {
		Member member = Member.builder()
			.memberId(java.util.UUID.randomUUID())
			.email("test@naver.com")
			.username("test")
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
	
	private Comment createCommentWithId(Long commentId, String content, PostEntity post) {
		Member member = Member.builder()
			.memberId(java.util.UUID.randomUUID())
			.email("test@naver.com")
			.username("test")
			.build();
		
		return Comment.builder()
			.commentId(commentId)
			.content(content)
			.member(member)
			.post(post)
			.build();
	}
}