package com.midas.shootpointer.domain.comment.business;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.BDDMockito.willDoNothing;
import static org.mockito.BDDMockito.willThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;

import com.midas.shootpointer.domain.comment.entity.Comment;
import com.midas.shootpointer.domain.comment.helper.CommentHelper;
import com.midas.shootpointer.domain.member.entity.Member;
import com.midas.shootpointer.domain.post.entity.PostEntity;
import com.midas.shootpointer.domain.post.helper.simple.PostHelper;
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
	
	@Test
	@DisplayName("댓글 삭제 성공 - 댓글 작성자가 삭제")
	void delete_Success() {
		// given
		Comment comment = createComment();
		Long commentId = comment.getCommentId();
		UUID memberId = comment.getMember().getMemberId();
		
		given(commentHelper.findCommentByCommentId(commentId)).willReturn(comment);
		willDoNothing().given(commentHelper).validateCommentOwner(comment, memberId);
		willDoNothing().given(commentHelper).delete(comment);
		
		// when
		commentManager.delete(commentId, memberId);
		
		// then
		then(commentHelper).should(times(1)).findCommentByCommentId(commentId);
		then(commentHelper).should(times(1)).validateCommentOwner(comment, memberId);
		then(commentHelper).should(times(1)).delete(comment);
	}
	
	@Test
	@DisplayName("댓글 삭제 실패 - 존재하지 않는 댓글")
	void delete_Failed_CommentNotFound() {
		// given
		Long commentId = 999L;
		UUID memberId = UUID.randomUUID();
		
		given(commentHelper.findCommentByCommentId(commentId))
			.willThrow(new CustomException(ErrorCode.IS_NOT_EXIST_COMMENT));
		
		// when-then
		assertThatThrownBy(() -> commentManager.delete(commentId, memberId))
			.isInstanceOf(CustomException.class)
			.hasFieldOrPropertyWithValue("errorCode", ErrorCode.IS_NOT_EXIST_COMMENT);
		
		then(commentHelper).should(times(1)).findCommentByCommentId(commentId);
		then(commentHelper).should(never()).validateCommentOwner(any(), any());
		then(commentHelper).should(never()).delete(any());
	}
	
	@Test
	@DisplayName("댓글 삭제 실패 - 댓글 작성자가 아닌 사용자")
	void delete_Failed_NotCommentOwner() {
		// given
		UUID otherMemberId = UUID.randomUUID();
		Comment comment = createComment();
		Long commentId = comment.getCommentId();
		
		given(commentHelper.findCommentByCommentId(commentId)).willReturn(comment);
		willThrow(new CustomException(ErrorCode.FORBIDDEN_COMMENT_ACCESS))
			.given(commentHelper).validateCommentOwner(comment, otherMemberId);
		
		// when-then
		assertThatThrownBy(() -> commentManager.delete(commentId, otherMemberId))
			.isInstanceOf(CustomException.class)
			.hasFieldOrPropertyWithValue("errorCode", ErrorCode.FORBIDDEN_COMMENT_ACCESS);
		
		then(commentHelper).should(times(1)).findCommentByCommentId(commentId);
		then(commentHelper).should(times(1)).validateCommentOwner(comment, otherMemberId);
		then(commentHelper).should(never()).delete(any());
	}
	
	@Test
	@DisplayName("댓글 삭제 실패 - null 댓글 ID")
	void delete_Failed_NullCommentId() {
		// given
		UUID memberId = UUID.randomUUID();
		
		given(commentHelper.findCommentByCommentId(null))
			.willThrow(new CustomException(ErrorCode.IS_NOT_EXIST_COMMENT));
		
		// when-then
		assertThatThrownBy(() -> commentManager.delete(null, memberId))
			.isInstanceOf(CustomException.class)
			.hasFieldOrPropertyWithValue("errorCode", ErrorCode.IS_NOT_EXIST_COMMENT);
		
		then(commentHelper).should(times(1)).findCommentByCommentId(null);
		then(commentHelper).should(never()).validateCommentOwner(any(), any());
		then(commentHelper).should(never()).delete(any());
	}
	
	@Test
	@DisplayName("댓글 삭제 실패 - null 회원 ID")
	void delete_Failed_NullMemberId() {
		// given
		Comment comment = createComment();
		Long commentId = comment.getCommentId();
		
		given(commentHelper.findCommentByCommentId(commentId)).willReturn(comment);
		willThrow(new CustomException(ErrorCode.FORBIDDEN_COMMENT_ACCESS))
			.given(commentHelper).validateCommentOwner(comment, null);
		
		// when-then
		assertThatThrownBy(() -> commentManager.delete(commentId, null))
			.isInstanceOf(CustomException.class)
			.hasFieldOrPropertyWithValue("errorCode", ErrorCode.FORBIDDEN_COMMENT_ACCESS);
		
		then(commentHelper).should(times(1)).findCommentByCommentId(commentId);
		then(commentHelper).should(times(1)).validateCommentOwner(comment, null);
		then(commentHelper).should(never()).delete(any());
	}
	
	@Test
	@DisplayName("여러 댓글 순차적 삭제 성공")
	void delete_Multiple_Success() {
		// given
		UUID memberId = UUID.randomUUID();
		Long commentId1 = 1L;
		Long commentId2 = 2L;
		Long commentId3 = 3L;
		
		Comment comment1 = createCommentWithOwner(commentId1, memberId);
		Comment comment2 = createCommentWithOwner(commentId2, memberId);
		Comment comment3 = createCommentWithOwner(commentId3, memberId);
		
		given(commentHelper.findCommentByCommentId(commentId1)).willReturn(comment1);
		given(commentHelper.findCommentByCommentId(commentId2)).willReturn(comment2);
		given(commentHelper.findCommentByCommentId(commentId3)).willReturn(comment3);
		
		willDoNothing().given(commentHelper).validateCommentOwner(comment1, memberId);
		willDoNothing().given(commentHelper).validateCommentOwner(comment2, memberId);
		willDoNothing().given(commentHelper).validateCommentOwner(comment3, memberId);
		
		willDoNothing().given(commentHelper).delete(comment1);
		willDoNothing().given(commentHelper).delete(comment2);
		willDoNothing().given(commentHelper).delete(comment3);
		
		// when
		commentManager.delete(commentId1, memberId);
		commentManager.delete(commentId2, memberId);
		commentManager.delete(commentId3, memberId);
		
		// then
		then(commentHelper).should(times(1)).delete(comment1);
		then(commentHelper).should(times(1)).delete(comment2);
		then(commentHelper).should(times(1)).delete(comment3);
	}
	
	@Test
	@DisplayName("댓글 수정 성공")
	void update_Success() {
		// given
		Long commentId = 1L;
		String newContent = "수정된 댓글입니다.";
		UUID memberId = UUID.randomUUID();
		Comment comment = createCommentWithOwner(commentId, memberId);
		
		given(commentHelper.findCommentByCommentId(commentId)).willReturn(comment);
		willDoNothing().given(commentHelper).validateCommentOwner(comment, memberId);
		willDoNothing().given(commentHelper).validateContentNotBlank(newContent);
		given(commentHelper.updateContent(comment, newContent)).willReturn(comment); // willReturn으로 변경
		
		// when
		Comment result = commentManager.update(commentId, newContent, memberId);
		
		// then
		assertThat(result).isNotNull();
		assertThat(result).isEqualTo(comment);
		then(commentHelper).should(times(1)).findCommentByCommentId(commentId);
		then(commentHelper).should(times(1)).validateCommentOwner(comment, memberId);
		then(commentHelper).should(times(1)).validateContentNotBlank(newContent);
		then(commentHelper).should(times(1)).updateContent(comment, newContent);
	}
	
	@Test
	@DisplayName("댓글 수정 실패 - 존재하지 않는 댓글")
	void update_Failed_CommentNotFound() {
		// given
		Long commentId = 999L;
		String newContent = "수정된 내용";
		UUID memberId = UUID.randomUUID();
		
		given(commentHelper.findCommentByCommentId(commentId))
			.willThrow(new CustomException(ErrorCode.IS_NOT_EXIST_COMMENT));
		
		// when-then
		assertThatThrownBy(() -> commentManager.update(commentId, newContent, memberId))
			.isInstanceOf(CustomException.class)
			.hasFieldOrPropertyWithValue("errorCode", ErrorCode.IS_NOT_EXIST_COMMENT);
		
		then(commentHelper).should(times(1)).findCommentByCommentId(commentId);
		then(commentHelper).should(never()).validateCommentOwner(any(), any());
		then(commentHelper).should(never()).validateContentNotBlank(any());
		then(commentHelper).should(never()).updateContent(any(), any());
	}
	
	@Test
	@DisplayName("댓글 수정 실패 - 댓글 작성자가 아닌 사용자")
	void update_Failed_NotCommentOwner() {
		// given
		Long commentId = 1L;
		String newContent = "수정된 내용";
		UUID ownerId = UUID.randomUUID();
		UUID otherMemberId = UUID.randomUUID();
		
		Comment comment = createCommentWithOwner(commentId, ownerId);
		
		given(commentHelper.findCommentByCommentId(commentId)).willReturn(comment);
		willThrow(new CustomException(ErrorCode.FORBIDDEN_COMMENT_ACCESS))
			.given(commentHelper).validateCommentOwner(comment, otherMemberId);
		
		// when-then
		assertThatThrownBy(() -> commentManager.update(commentId, newContent, otherMemberId))
			.isInstanceOf(CustomException.class)
			.hasFieldOrPropertyWithValue("errorCode", ErrorCode.FORBIDDEN_COMMENT_ACCESS);
		
		then(commentHelper).should(times(1)).findCommentByCommentId(commentId);
		then(commentHelper).should(times(1)).validateCommentOwner(comment, otherMemberId);
		then(commentHelper).should(never()).validateContentNotBlank(any());
		then(commentHelper).should(never()).updateContent(any(), any());
	}
	
	@Test
	@DisplayName("댓글 수정 실패 - 빈 내용")
	void update_Failed_BlankContent() {
		// given
		Long commentId = 1L;
		String blankContent = "   ";
		UUID memberId = UUID.randomUUID();
		
		Comment comment = createCommentWithOwner(commentId, memberId);
		
		given(commentHelper.findCommentByCommentId(commentId)).willReturn(comment);
		willDoNothing().given(commentHelper).validateCommentOwner(comment, memberId);
		willThrow(new CustomException(ErrorCode.INVALID_INPUT_VALUE))
			.given(commentHelper).validateContentNotBlank(blankContent);
		
		// when-then
		assertThatThrownBy(() -> commentManager.update(commentId, blankContent, memberId))
			.isInstanceOf(CustomException.class)
			.hasFieldOrPropertyWithValue("errorCode", ErrorCode.INVALID_INPUT_VALUE);
		
		then(commentHelper).should(times(1)).findCommentByCommentId(commentId);
		then(commentHelper).should(times(1)).validateCommentOwner(comment, memberId);
		then(commentHelper).should(times(1)).validateContentNotBlank(blankContent);
		then(commentHelper).should(never()).updateContent(any(), any());
	}
	
	@Test
	@DisplayName("댓글 수정 실패 - null 내용")
	void update_Failed_NullContent() {
		// given
		Long commentId = 1L;
		UUID memberId = UUID.randomUUID();
		
		Comment comment = createCommentWithOwner(commentId, memberId);
		
		given(commentHelper.findCommentByCommentId(commentId)).willReturn(comment);
		willDoNothing().given(commentHelper).validateCommentOwner(comment, memberId);
		willThrow(new CustomException(ErrorCode.INVALID_INPUT_VALUE))
			.given(commentHelper).validateContentNotBlank(null);
		
		// when-then
		assertThatThrownBy(() -> commentManager.update(commentId, null, memberId))
			.isInstanceOf(CustomException.class)
			.hasFieldOrPropertyWithValue("errorCode", ErrorCode.INVALID_INPUT_VALUE);
		
		then(commentHelper).should(times(1)).findCommentByCommentId(commentId);
		then(commentHelper).should(times(1)).validateCommentOwner(comment, memberId);
		then(commentHelper).should(times(1)).validateContentNotBlank(null);
		then(commentHelper).should(never()).updateContent(any(), any());
	}
	
	@Test
	@DisplayName("여러 댓글 순차적 수정 성공")
	void update_Multiple_Success() {
		// given
		UUID memberId = UUID.randomUUID();
		Long commentId1 = 1L;
		Long commentId2 = 2L;
		String content1 = "첫 번째 수정 내용";
		String content2 = "두 번째 수정 내용";
		
		Comment comment1 = createCommentWithOwner(commentId1, memberId);
		Comment comment2 = createCommentWithOwner(commentId2, memberId);
		
		given(commentHelper.findCommentByCommentId(commentId1)).willReturn(comment1);
		given(commentHelper.findCommentByCommentId(commentId2)).willReturn(comment2);
		
		willDoNothing().given(commentHelper).validateCommentOwner(comment1, memberId);
		willDoNothing().given(commentHelper).validateCommentOwner(comment2, memberId);
		
		willDoNothing().given(commentHelper).validateContentNotBlank(content1);
		willDoNothing().given(commentHelper).validateContentNotBlank(content2);
		
		given(commentHelper.updateContent(comment1, content1)).willReturn(comment1);
		given(commentHelper.updateContent(comment2, content2)).willReturn(comment2);
		
		// when
		Comment result1 = commentManager.update(commentId1, content1, memberId);
		Comment result2 = commentManager.update(commentId2, content2, memberId);
		
		// then
		assertThat(result1).isNotNull().isEqualTo(comment1);
		assertThat(result2).isNotNull().isEqualTo(comment2);
		then(commentHelper).should(times(1)).updateContent(comment1, content1);
		then(commentHelper).should(times(1)).updateContent(comment2, content2);
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
	
	private Comment createCommentWithOwner(Long commentId, UUID ownerId) {
		Member owner = Member.builder()
			.memberId(ownerId)
			.email("owner@test.com")
			.username("owner")
			.build();
		
		PostEntity post = PostEntity.builder()
			.postId(1L)
			.build();
		
		return Comment.builder()
			.commentId(commentId)
			.content("테스트 댓글")
			.member(owner)
			.post(post)
			.build();
	}
}