package com.midas.shootpointer.domain.comment.business.command;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.BDDMockito.willDoNothing;
import static org.mockito.Mockito.times;

import com.midas.shootpointer.domain.comment.business.CommentManager;
import com.midas.shootpointer.domain.comment.entity.Comment;
import com.midas.shootpointer.domain.member.entity.Member;
import com.midas.shootpointer.domain.post.entity.PostEntity;
import java.util.UUID;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
@DisplayName("CommentCommandService 테스트")
class CommentCommandServiceImplTest {
	
	@Mock
	private CommentManager commentManager;
	
	@InjectMocks
	private CommentCommandServiceImpl commentCommandService;
	
	@Test
	@DisplayName("댓글 생성 성공")
	void create_success() {
		// given
		Comment comment = createComment();
		Long expectedCommentId = 1L;
		
		given(commentManager.save(any(Comment.class))).willReturn(expectedCommentId);
		
		// when
		Long result = commentCommandService.create(comment);
		
		// then
		assertThat(result).isEqualTo(expectedCommentId);
		then(commentManager).should().save(comment);
	}
	
	@Test
	@DisplayName("댓글 삭제 성공")
	void delete_Success() {
		// given
		Long commentId = 1L;
		UUID memberId = UUID.randomUUID();
		
		willDoNothing().given(commentManager).delete(commentId, memberId);
		
		// when
		commentCommandService.delete(commentId, memberId);
		
		// then
		then(commentManager).should(times(1)).delete(commentId, memberId);
	}
	
	@Test
	@DisplayName("여러 댓글 삭제 성공")
	void delete_Multiple_Success() {
		// given
		UUID memberId = UUID.randomUUID();
		Long commentId1 = 1L;
		Long commentId2 = 2L;
		Long commentId3 = 3L;
		
		willDoNothing().given(commentManager).delete(commentId1, memberId);
		willDoNothing().given(commentManager).delete(commentId2, memberId);
		willDoNothing().given(commentManager).delete(commentId3, memberId);
		
		// when
		commentCommandService.delete(commentId1, memberId);
		commentCommandService.delete(commentId2, memberId);
		commentCommandService.delete(commentId3, memberId);
		
		// then
		then(commentManager).should(times(1)).delete(commentId1, memberId);
		then(commentManager).should(times(1)).delete(commentId2, memberId);
		then(commentManager).should(times(1)).delete(commentId3, memberId);
	}
	
	@Test
	@DisplayName("댓글 수정 성공")
	void update_Success() {
		// given
		UUID memberId = UUID.randomUUID();
		Long commentId = 1L;
		String content = "수정된 댓글 내용입니다.";
		
		willDoNothing().given(commentManager).update(commentId, content, memberId);
		
		// when
		commentCommandService.update(commentId, content, memberId);
		
		// then
		then(commentManager).should(times(1)).update(commentId, content, memberId);
	}
	
	@Test
	@DisplayName("여러 댓글 수정 성공")
	void update_Multiple_Success() {
		// given
		UUID memberId = UUID.randomUUID();
		Long commentId1 = 1L;
		Long commentId2 = 2L;
		String content1 = "첫번째 댓글 수정";
		String content2 = "두번째 댓글 수정";
		
		willDoNothing().given(commentManager).update(commentId1, content1, memberId);
		willDoNothing().given(commentManager).update(commentId2, content2, memberId);
		
		// when
		commentCommandService.update(commentId1, content1, memberId);
		commentCommandService.update(commentId2, content2, memberId);
		
		// then
		then(commentManager).should(times(1)).update(commentId1, content1, memberId);
		then(commentManager).should(times(1)).update(commentId2, content2, memberId);
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
}