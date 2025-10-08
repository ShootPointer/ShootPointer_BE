package com.midas.shootpointer.domain.comment.helper;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatNoException;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.BDDMockito.willDoNothing;
import static org.mockito.BDDMockito.willThrow;

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
@DisplayName("CommentHelper 테스트")
class CommentHelperImplTest {
	
	@Mock
	private CommentValidation commentValidation;
	
	@Mock
	private CommentUtil commentUtil;
	
	@InjectMocks
	private CommentHelperImpl commentHelper;
	
	@Test
	@DisplayName("게시물 존재 여부 검증 성공")
	void validatePostExists_Success() {
		// given
		Long postId = 1L;
		willDoNothing().given(commentValidation).validatePostExists(postId);
		
		// when-then
		assertThatNoException().isThrownBy(() -> commentHelper.validatePostExists(postId));
		then(commentValidation).should().validatePostExists(postId);
	}
	
	@Test
	@DisplayName("게시물 존재 여부 검증 실패 - 예외 발생")
	void validatePostExists_ThrowException() {
		// given
		Long postId = 999_999_9L;
		willThrow(new CustomException(ErrorCode.IS_NOT_EXIST_POST))
			.given(commentValidation).validatePostExists(postId);
		
		// when-then
		assertThatThrownBy(() -> commentHelper.validatePostExists(postId))
			.isInstanceOf(CustomException.class)
			.hasFieldOrPropertyWithValue("errorCode", ErrorCode.IS_NOT_EXIST_POST);
		
		then(commentValidation).should().validatePostExists(postId);
	}
	
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
		
		given(commentUtil.save(comment)).willReturn(savedComment);
		
		// when
		Comment result = commentHelper.save(comment);
		
		// then
		assertThat(result).isEqualTo(savedComment);
		assertThat(result.getCommentId()).isEqualTo(1L);
		then(commentUtil).should().save(comment);
	}
	
	@Test
	@DisplayName("게시물 ID로 댓글 목록 조회 성공")
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
		
		given(commentUtil.findAllByPostIdOrderByCreatedAtDesc(postId)).willReturn(comments);
		
		// when
		List<Comment> result = commentHelper.findAllByPostIdOrderByCreatedAtDesc(postId);
		
		// then
		assertThat(result).hasSize(3);
		assertThat(result.get(0).getCommentId()).isEqualTo(3L);
		assertThat(result.get(1).getCommentId()).isEqualTo(2L);
		assertThat(result.get(2).getCommentId()).isEqualTo(1L);
		then(commentUtil).should().findAllByPostIdOrderByCreatedAtDesc(postId);
	}
	
	@Test
	@DisplayName("댓글 목록 조회 성공 - 빈 리스트 반환")
	void findAllByPostIdOrderByCreatedAtDesc_Success_EmptyList() {
		// given
		Long postId = 1L;
		given(commentUtil.findAllByPostIdOrderByCreatedAtDesc(postId)).willReturn(List.of());
		
		// when
		List<Comment> result = commentHelper.findAllByPostIdOrderByCreatedAtDesc(postId);
		
		// then
		assertThat(result).isEmpty();
		then(commentUtil).should().findAllByPostIdOrderByCreatedAtDesc(postId);
	}
	
	@Test
	@DisplayName("댓글 ID로 댓글 조회 성공")
	void findCommentByCommentId_Success() {
		// given
		Comment comment = createComment();
		Long commentId = comment.getCommentId();
		
		given(commentUtil.findCommentByCommentId(commentId)).willReturn(comment);
		
		// when
		Comment result = commentHelper.findCommentByCommentId(commentId);
		
		// then
		assertThat(result).isEqualTo(comment);
		assertThat(result.getCommentId()).isEqualTo(commentId);
		then(commentUtil).should().findCommentByCommentId(commentId);
	}
	
	@Test
	@DisplayName("댓글 ID로 댓글 조회 실패 - 존재하지 않는 댓글")
	void findCommentByCommentId_Failed_NotFound() {
		// given
		Long commentId = 999L;
		
		given(commentUtil.findCommentByCommentId(commentId))
			.willThrow(new CustomException(ErrorCode.IS_NOT_EXIST_COMMENT));
		
		// when-then
		assertThatThrownBy(() -> commentHelper.findCommentByCommentId(commentId))
			.isInstanceOf(CustomException.class)
			.hasFieldOrPropertyWithValue("errorCode", ErrorCode.IS_NOT_EXIST_COMMENT);
		
		then(commentUtil).should().findCommentByCommentId(commentId);
	}
	
	@Test
	@DisplayName("댓글 삭제 성공")
	void delete_Success() {
		// given
		Comment comment = createComment();
		
		willDoNothing().given(commentUtil).delete(comment);
		
		// when
		commentHelper.delete(comment);
		
		// then
		then(commentUtil).should().delete(comment);
	}
	
	@Test
	@DisplayName("댓글 작성자 검증 성공")
	void validateCommentOwner_Success() {
		// given
		Comment comment = createComment();
		UUID memberId = comment.getMember().getMemberId();
		
		willDoNothing().given(commentValidation).validateCommentOwner(comment, memberId);
		
		// when
		commentHelper.validateCommentOwner(comment, memberId);
		
		// then
		then(commentValidation).should().validateCommentOwner(comment, memberId);
	}
	
	@Test
	@DisplayName("댓글 작성자 검증 실패 - 권한 없음")
	void validateCommentOwner_Failed_Forbidden() {
		// given
		UUID otherMemberId = UUID.randomUUID();
		Comment comment = createComment();
		
		willThrow(new CustomException(ErrorCode.FORBIDDEN_COMMENT_ACCESS))
			.given(commentValidation).validateCommentOwner(comment, otherMemberId);
		
		// when-then
		assertThatThrownBy(() -> commentHelper.validateCommentOwner(comment, otherMemberId))
			.isInstanceOf(CustomException.class)
			.hasFieldOrPropertyWithValue("errorCode", ErrorCode.FORBIDDEN_COMMENT_ACCESS);
		
		then(commentValidation).should().validateCommentOwner(comment, otherMemberId);
	}
	
	private Comment createComment() {
		Member member = Member.builder()
			.memberId(UUID.randomUUID())
			.email("test@naver.com")
			.username("test")
			.build();
		
		PostEntity post = PostEntity.builder()
			.postId(1L)
			.build();
		
		return Comment.builder()
			.content("테스트입니다.")
			.member(member)
			.post(post)
			.build();
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