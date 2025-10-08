package com.midas.shootpointer.domain.comment.helper;

import static org.assertj.core.api.Assertions.assertThatNoException;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;

import com.midas.shootpointer.domain.comment.entity.Comment;
import com.midas.shootpointer.domain.member.entity.Member;
import com.midas.shootpointer.domain.post.entity.PostEntity;
import com.midas.shootpointer.domain.post.helper.PostHelper;
import com.midas.shootpointer.global.common.ErrorCode;
import com.midas.shootpointer.global.exception.CustomException;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
@DisplayName("CommentValidation 테스트")
class CommentValidationImplTest {
	
	@Mock
	private PostHelper postHelper;
	
	@InjectMocks
	private CommentValidationImpl commentValidation;
	
	@Test
	@DisplayName("존재하는 게시물 검증 성공")
	void validatePostExists_ValidPost_Success() {
		// given
		Long postId = 1L;
		PostEntity postEntity = PostEntity.builder()
			.postId(postId)
			.build();
		
		given(postHelper.findPostByPostId(postId)).willReturn(postEntity);
		
		// when-then
		assertThatNoException().isThrownBy(() ->
			commentValidation.validatePostExists(postId));
	}
	
	@Test
	@DisplayName("존재하지 않는 게시물 검증 실패 - 예외 발생")
	void validatePostExists_InvalidPost_ThrowException() {
		// given
		Long postId = 999L;
		
		given(postHelper.findPostByPostId(postId))
			.willThrow(new CustomException(ErrorCode.IS_NOT_EXIST_POST));
		
		// when-then
		assertThatThrownBy(() -> commentValidation.validatePostExists(postId))
			.isInstanceOf(CustomException.class)
			.hasFieldOrPropertyWithValue("errorCode", ErrorCode.IS_NOT_EXIST_POST);
	}
	
	@Test
	@DisplayName("PostHelper에서 다른 예외 발생 시 IS_NOT_EXIST_POST로 변환")
	void validatePostExists_PostHelperException_ConvertToPostNotFound() {
		// given
		Long postId = 1L;
		
		given(postHelper.findPostByPostId(postId))
			.willThrow(new RuntimeException("Database connection error"));
		
		// when-then
		assertThatThrownBy(() -> commentValidation.validatePostExists(postId))
			.isInstanceOf(CustomException.class)
			.hasFieldOrPropertyWithValue("errorCode", ErrorCode.IS_NOT_EXIST_POST);
	}
	
	@Test
	@DisplayName("댓글 작성자 검증 성공 - 동일한 작성자")
	void validateCommentOwner_Success() {
		// given
		UUID memberId = UUID.randomUUID();
		Member member = Member.builder()
			.memberId(memberId)
			.build();
		
		Comment comment = Comment.builder()
			.member(member)
			.build();
		
		// when-then
		assertThatNoException().isThrownBy(() ->
			commentValidation.validateCommentOwner(comment, memberId));
	}
	
	@Test
	@DisplayName("댓글 작성자 검증 실패 - 다른 작성자")
	void validateCommentOwner_Failed_Forbidden() {
		// given
		UUID commentOwnerId = UUID.randomUUID();
		UUID InvalidOwnerId = UUID.randomUUID(); // 다른 UUID
		
		Member member = Member.builder()
			.memberId(commentOwnerId)
			.build();
		
		Comment comment = Comment.builder()
			.member(member)
			.build();
		
		// when-then
		assertThatThrownBy(() -> commentValidation.validateCommentOwner(comment, InvalidOwnerId))
			.isInstanceOf(CustomException.class)
			.hasFieldOrPropertyWithValue("errorCode", ErrorCode.FORBIDDEN_COMMENT_ACCESS);
	}
	
}