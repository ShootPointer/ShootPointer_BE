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
import java.util.Arrays;
import java.util.List;
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
		given(commentHelper.save(any(Comment.class))).willReturn(savedComment);
		
		// when
		Long result = commentManager.save(comment);
		
		// then
		assertThat(result).isEqualTo(1L);
		then(postHelper).should().findPostByPostId(comment.getPost().getPostId());
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
	
	@Test
	@DisplayName("게시물 ID로 댓글 목록 조회 성공")
	void findCommentsByPostId_Success() {
		// given
		Long postId = 1L;
		PostEntity post = PostEntity.builder()
			.postId(postId)
			.build();
		
		List<Comment> comments = Arrays.asList(
			createCommentWithId(1L, "첫 번째 댓글", post),
			createCommentWithId(2L, "두 번째 댓글", post),
			createCommentWithId(3L, "세 번째 댓글", post)
		);
		
		given(postHelper.findPostByPostId(postId)).willReturn(post);
		given(commentHelper.findAllByPostIdOrderByCreatedAtDesc(postId)).willReturn(comments);
		
		// when
		List<Comment> result = commentManager.findCommentsByPostId(postId);
		
		// then
		assertThat(result).hasSize(3);
		assertThat(result).isEqualTo(comments);
		then(postHelper).should().findPostByPostId(postId);
		then(commentHelper).should().findAllByPostIdOrderByCreatedAtDesc(postId);
	}
	
	@Test
	@DisplayName("댓글 목록 조회 성공 - 댓글이 없는 경우")
	void findCommentsByPostId_Success_EmptyList() {
		// given
		Long postId = 1L;
		PostEntity post = PostEntity.builder()
			.postId(postId)
			.build();
		
		given(postHelper.findPostByPostId(postId)).willReturn(post);
		given(commentHelper.findAllByPostIdOrderByCreatedAtDesc(postId)).willReturn(List.of());
		
		// when
		List<Comment> result = commentManager.findCommentsByPostId(postId);
		
		// then
		assertThat(result).isEmpty();
		then(postHelper).should().findPostByPostId(postId);
		then(commentHelper).should().findAllByPostIdOrderByCreatedAtDesc(postId);
	}
	
	@Test
	@DisplayName("댓글 목록 조회 실패 - 존재하지 않는 게시물")
	void findCommentsByPostId_Failed_PostNotFound() {
		// given
		Long postId = 999L;
		
		given(postHelper.findPostByPostId(postId)).willThrow(
			new CustomException(ErrorCode.IS_NOT_EXIST_POST)
		);
		
		// when-then
		assertThatThrownBy(() -> commentManager.findCommentsByPostId(postId))
			.isInstanceOf(CustomException.class)
			.hasFieldOrPropertyWithValue("errorCode", ErrorCode.IS_NOT_EXIST_POST);
		
		then(postHelper).should().findPostByPostId(postId);
		then(commentHelper).should(never()).findAllByPostIdOrderByCreatedAtDesc(anyLong());
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
	
	private Member createMember() {
		return Member.builder()
			.memberId(java.util.UUID.randomUUID())
			.email("test@naver.com")
			.username("test")
			.build();
	}
}