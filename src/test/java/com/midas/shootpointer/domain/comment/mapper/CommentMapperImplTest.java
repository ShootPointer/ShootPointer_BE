package com.midas.shootpointer.domain.comment.mapper;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

import com.midas.shootpointer.domain.comment.dto.request.CommentRequestDto;
import com.midas.shootpointer.domain.comment.entity.Comment;
import com.midas.shootpointer.domain.member.entity.Member;
import com.midas.shootpointer.domain.post.entity.PostEntity;
import java.util.UUID;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

@ExtendWith(MockitoExtension.class)
@ActiveProfiles("dev")
@DisplayName("CommentMapper 테스트")
class CommentMapperImplTest {
	
	@InjectMocks
	private CommentMapperImpl commentMapper;
	
	@Test
	@DisplayName("DTO -> Entity 변환 성공")
	void dtoToEntity_Success() {
		// given
		CommentRequestDto requestDto = CommentRequestDto.builder()
			.postId(1L)
			.content("테스트입니다.")
			.build();
		
		Member member = Member.builder()
			.memberId(UUID.randomUUID())
			.email("test@naver.com")
			.username("test")
			.build();
		
		PostEntity post = PostEntity.builder()
			.postId(1L)
			.build();
		
		// when
		Comment result = commentMapper.dtoToEntity(requestDto, member, post);
		
		// then
		assertThat(result).isNotNull();
		assertThat(result.getContent()).isEqualTo(requestDto.getContent());
		assertThat(result.getMember()).isEqualTo(member);
		assertThat(result.getPost()).isEqualTo(post);
		assertThat(result.getCommentId()).isNull();
	}
	
	@Test
	@DisplayName("DTO -> Entity 변환 실패 _ DTO가 Null인 경우")
	void dtoToEntity_Failed_NullDTO() {
		// given
		CommentRequestDto requestDto = null;
		
		Member member = Member.builder()
			.memberId(UUID.randomUUID())
			.email("test@naver.com")
			.username("test")
			.build();
		
		PostEntity post = PostEntity.builder()
			.postId(1L)
			.build();
		
		// when-then
		assertThatThrownBy(() -> commentMapper.dtoToEntity(requestDto, member, post))
			.isInstanceOf(NullPointerException.class);
	}
	
	@Test
	@DisplayName("DTO -> Entity 변환 실패 _ Member가 Null인 경우")
	void dtoToEntity_Failed_NullMember() {
		// given
		CommentRequestDto requestDto = CommentRequestDto.builder()
			.postId(1L)
			.content("테스트입니다.")
			.build();
		
		Member member = null;
		PostEntity post = PostEntity.builder()
			.postId(1L)
			.build();
		
		// when
		Comment result = commentMapper.dtoToEntity(requestDto, member, post);
		
		// then
		assertThat(result).isNotNull();
		assertThat(result.getContent()).isEqualTo(requestDto.getContent());
		assertThat(result.getMember()).isNull();
		assertThat(result.getPost()).isEqualTo(post);
	}
	
	@Test
	@DisplayName("DTO -> Entity 변환 실패 - Post가 Null인 경우")
	void dtoToEntity_Failed_NullPost() {
		// given
		CommentRequestDto requestDto = CommentRequestDto.builder()
			.postId(1L)
			.content("테스트입니다.")
			.build();
		Member member = Member.builder()
			.memberId(UUID.randomUUID())
			.email("test@naver.com")
			.username("test")
			.build();
		PostEntity post = null;
		
		// when
		Comment result = commentMapper.dtoToEntity(requestDto, member, post);
		
		// then
		assertThat(result).isNotNull();
		assertThat(result.getContent()).isEqualTo(requestDto.getContent());
		assertThat(result.getMember()).isEqualTo(member);
		assertThat(result.getPost()).isNull();
	}
}