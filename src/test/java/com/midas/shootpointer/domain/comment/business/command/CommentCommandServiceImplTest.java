package com.midas.shootpointer.domain.comment.business.command;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

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
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;

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