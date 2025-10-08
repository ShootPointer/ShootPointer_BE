package com.midas.shootpointer.domain.comment.business.query;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

import com.midas.shootpointer.domain.comment.business.CommentManager;
import com.midas.shootpointer.domain.comment.entity.Comment;
import com.midas.shootpointer.domain.member.entity.Member;
import com.midas.shootpointer.domain.post.entity.PostEntity;
import com.midas.shootpointer.global.common.ErrorCode;
import com.midas.shootpointer.global.exception.CustomException;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
@DisplayName("CommentQueryServiceImpl 테스트")
class CommentQueryServiceImplTest {
	
	@Mock
	private CommentManager commentManager;
	
	@InjectMocks
	private CommentQueryServiceImpl commentQueryService;
	
	@Test
	@DisplayName("게시물 ID로 댓글 목록 조회_Success")
	void getCommentsByPostId_Success() {
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
		
		given(commentManager.findCommentsByPostId(postId)).willReturn(comments);
		
		// when
		List<Comment> result = commentQueryService.getCommentsByPostId(postId);
		
		// then
		assertThat(result).hasSize(3);
		assertThat(result.get(0).getCommentId()).isEqualTo(3L);
		assertThat(result.get(0).getContent()).isEqualTo("최신 댓글");
		assertThat(result.get(1).getCommentId()).isEqualTo(2L);
		assertThat(result.get(2).getCommentId()).isEqualTo(1L);
		then(commentManager).should().findCommentsByPostId(postId);
	}
	
	@Test
	@DisplayName("댓글 목록 조회_Success - 빈 리스트 반환")
	void getCommentsByPostId_Success_EmptyList() {
		// given
		Long postId = 1L;
		given(commentManager.findCommentsByPostId(postId)).willReturn(List.of());
		
		// when
		List<Comment> result = commentQueryService.getCommentsByPostId(postId);
		
		// then
		assertThat(result).isEmpty();
		then(commentManager).should().findCommentsByPostId(postId);
	}
	
	@Test
	@DisplayName("댓글 목록 조회_Failed - 존재하지 않는 게시물")
	void getCommentsByPostId_Failed_PostNotFound() {
		// given
		Long postId = 999L;
		given(commentManager.findCommentsByPostId(postId)).willThrow(
			new CustomException(ErrorCode.IS_NOT_EXIST_POST)
		);
		
		// when-then
		assertThatThrownBy(() -> commentQueryService.getCommentsByPostId(postId))
			.isInstanceOf(CustomException.class)
			.hasFieldOrPropertyWithValue("errorCode", ErrorCode.IS_NOT_EXIST_POST);
		
		then(commentManager).should().findCommentsByPostId(postId);
	}
	
	private Comment createCommentWithId(Long commentId, String content, PostEntity post) {
		Member member = Member.builder()
			.memberId(UUID.randomUUID())
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