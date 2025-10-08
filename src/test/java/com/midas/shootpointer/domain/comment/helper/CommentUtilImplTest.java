package com.midas.shootpointer.domain.comment.helper;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.BDDMockito.willThrow;
import static org.mockito.Mockito.times;

import com.midas.shootpointer.domain.comment.entity.Comment;
import com.midas.shootpointer.domain.comment.repository.command.CommentCommandRepository;
import com.midas.shootpointer.domain.comment.repository.query.CommentQueryRepository;
import com.midas.shootpointer.domain.member.entity.Member;
import com.midas.shootpointer.domain.post.entity.PostEntity;
import com.midas.shootpointer.global.common.ErrorCode;
import com.midas.shootpointer.global.exception.CustomException;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
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
	
	@Test
	@DisplayName("댓글 ID로 댓글 조회 성공")
	void findCommentByCommentId_Success() {
		// given
		Comment comment = createComment();
		Long commentId = comment.getCommentId();
		
		given(commentQueryRepository.findCommentByCommentId(commentId))
			.willReturn(Optional.of(comment));
		
		// when
		Comment result = commentUtil.findCommentByCommentId(commentId);
		
		// then
		assertThat(result).isEqualTo(comment);
		assertThat(result.getCommentId()).isEqualTo(commentId);
		assertThat(result.getContent()).isEqualTo("테스트 댓글입니다.");
		then(commentQueryRepository).should().findCommentByCommentId(commentId);
	}
	
	@Test
	@DisplayName("댓글 ID로 댓글 조회 실패 - 존재하지 않는 댓글")
	void findCommentByCommentId_Failed_NotFound() {
		// given
		Long commentId = 999L;
		
		given(commentQueryRepository.findCommentByCommentId(commentId))
			.willReturn(Optional.empty());
		
		// when-then
		assertThatThrownBy(() -> commentUtil.findCommentByCommentId(commentId))
			.isInstanceOf(CustomException.class)
			.hasFieldOrPropertyWithValue("errorCode", ErrorCode.IS_NOT_EXIST_COMMENT);
		
		then(commentQueryRepository).should().findCommentByCommentId(commentId);
	}
	
	@Test
	@DisplayName("댓글 삭제 성공 - soft delete 수행")
	void delete_Success() {
		// given
		Comment comment = createComment();
		
		given(commentCommandRepository.save(comment)).willReturn(comment);
		
		// when
		commentUtil.delete(comment);
		
		// then
		then(commentCommandRepository).should(times(1)).save(comment);
	}
	
	@Test
	@DisplayName("여러 댓글 삭제 성공")
	void delete_Multiple_Success() {
		// given
		Comment comment1 = createComment();
		Comment comment2 = createComment();
		Comment comment3 = createComment();
		
		given(commentCommandRepository.save(any(Comment.class)))
			.willAnswer(invocation -> invocation.getArgument(0));
		
		// when
		commentUtil.delete(comment1);
		commentUtil.delete(comment2);
		commentUtil.delete(comment3);
		
		// then
		then(commentCommandRepository).should(times(3)).save(any(Comment.class));
	}
	
	@Test
	@DisplayName("댓글 내용 업데이트 성공")
	void updateContent_Success() {
		// given
		Comment comment = createComment();
		String newContent = "수정된 댓글 내용";
		
		given(commentCommandRepository.save(comment)).willReturn(comment);
		
		// when
		commentUtil.updateContent(comment, newContent);
		
		// then
		then(commentCommandRepository).should(times(1)).save(comment);
	}
	
	@Test
	@DisplayName("여러 댓글 내용 업데이트 성공")
	void updateContent_Multiple_Success() {
		// given
		Comment comment1 = createComment();
		Comment comment2 = createComment();
		String content1 = "첫 번째 수정";
		String content2 = "두 번째 수정";
		
		given(commentCommandRepository.save(any(Comment.class)))
			.willAnswer(invocation -> invocation.getArgument(0));
		
		// when
		commentUtil.updateContent(comment1, content1);
		commentUtil.updateContent(comment2, content2);
		
		// then
		then(commentCommandRepository).should(times(2)).save(any(Comment.class));
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