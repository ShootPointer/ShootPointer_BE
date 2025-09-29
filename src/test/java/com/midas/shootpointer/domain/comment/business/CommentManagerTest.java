package com.midas.shootpointer.domain.comment.business;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.BDDMockito.willDoNothing;
import static org.mockito.BDDMockito.willThrow;
import static org.mockito.Mockito.never;

import com.midas.shootpointer.domain.comment.entity.Comment;
import com.midas.shootpointer.domain.comment.helper.CommentHelper;
import com.midas.shootpointer.domain.member.entity.Member;
import com.midas.shootpointer.domain.post.entity.PostEntity;
import com.midas.shootpointer.domain.post.helper.PostHelper;
import com.midas.shootpointer.global.common.ErrorCode;
import com.midas.shootpointer.global.exception.CustomException;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
@DisplayName("CommentManager 테스트")
class CommentManagerTest {
	
	@Mock
	private CommentHelper commentHelper;
	
	@Mock
	private PostHelper postHelper;
	
	@InjectMocks
	private CommentManager commentManager;
	
	@Test
	@DisplayName("댓글 저장 성공")
	void comment_save_success() {
		// given
		Comment comment = createComment();
		Comment savedComment = Comment.builder()
			.commentId(1L)
			.content(comment.getContent())
			.member(comment.getMember())
			.post(comment.getPost())
			.build();
		
		given(postHelper.findPostByPostId(anyLong())).willReturn(comment.getPost());
		willDoNothing().given(commentHelper).isValidateCommentContent(anyString());
		given(commentHelper.save(any(Comment.class))).willReturn(savedComment);
		
		// when
		Long result = commentManager.save(comment);
		
		// then
		assertThat(result).isEqualTo(1L);
		then(postHelper).should().findPostByPostId(comment.getPost().getPostId());
		then(commentHelper).should().isValidateCommentContent(comment.getContent());
		then(commentHelper).should().save(comment);
	}
	
	@Test
	@DisplayName("존재하지 않는 게시물에 댓글 작성 -> 에외 발생")
	void save_IS_NOT_EXIST_POST_ThrowException() {
		// given
		Comment comment = createComment();
		
		given(postHelper.findPostByPostId(anyLong())).willThrow(
			new CustomException(ErrorCode.IS_NOT_EXIST_POST)
		);
		
		// when-then
		assertThatThrownBy(() -> commentManager.save(comment))
			.isInstanceOf(CustomException.class)
			.hasFieldOrPropertyWithValue("errorCode", ErrorCode.IS_NOT_EXIST_POST);
		
		then(postHelper).should().findPostByPostId(comment.getPost().getPostId());
		then(commentHelper).shouldHaveNoInteractions();
	}
	
	@Test
	@DisplayName("유효하지 않은 댓글 내용으로 저장 -> 예외 발생")
	void save_Invalid_Content_ThrowException() {
		// given
		Comment comment = createComment();
		
		given(postHelper.findPostByPostId(anyLong())).willReturn(comment.getPost());
		willThrow(new CustomException(ErrorCode.INVALID_COMMENT_CONTENT))
			.given(commentHelper).isValidateCommentContent(anyString());
		
		// when-then
		assertThatThrownBy(() -> commentManager.save(comment))
			.isInstanceOf(CustomException.class)
			.hasFieldOrPropertyWithValue("errorCode", ErrorCode.INVALID_COMMENT_CONTENT);
		
		then(postHelper).should().findPostByPostId(comment.getPost().getPostId());
		then(commentHelper).should().isValidateCommentContent(comment.getContent());
		then(commentHelper).should(never()).save(any(Comment.class));
	}
	
	@Test
	@DisplayName("댓글 저장 실패 - Comment가 Null인 경우")
	void save_Failed_NullComment() {
		// given
		Comment comment = null;
		
		// when-then
		assertThatThrownBy(() -> commentManager.save(comment))
			.isInstanceOf(NullPointerException.class);
	}
	
	@Test
	@DisplayName("댓글 저장 실패 - Comment에 Post가 Null인 경우")
	void save_Failed_CommentPostIsNull() {
		// given
		Comment comment = Comment.builder()
			.content("테스트 댓글")
			.member(createMember())
			.post(null)
			.build();
		
		// when-then
		assertThatThrownBy(() -> commentManager.save(comment))
			.isInstanceOf(NullPointerException.class);
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
	
	private Member createMember() {
		return Member.builder()
			.memberId(java.util.UUID.randomUUID())
			.email("test@test.com")
			.username("testUser")
			.build();
	}
}