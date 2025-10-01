package com.midas.shootpointer.domain.comment.mapper;

import static org.assertj.core.api.Assertions.*;

import com.midas.shootpointer.domain.comment.dto.request.CommentRequestDto;
import com.midas.shootpointer.domain.comment.dto.response.CommentResponseDto;
import com.midas.shootpointer.domain.comment.entity.Comment;
import com.midas.shootpointer.domain.member.entity.Member;
import com.midas.shootpointer.domain.post.entity.PostEntity;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
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
	
	@Test
	@DisplayName("Entity -> DTO 변환 성공")
	void entityToDto_Success() {
		// given
		Member member = Member.builder()
			.memberId(UUID.randomUUID())
			.email("test@naver.com")
			.username("test")
			.build();
		
		PostEntity post = PostEntity.builder()
			.postId(1L)
			.build();
		
		Comment comment = Comment.builder()
			.commentId(1L)
			.content("테스트 댓글입니다.")
			.member(member)
			.post(post)
			.build();
		
		// when
		CommentResponseDto result = commentMapper.entityToDto(comment);
		
		// then
		assertThat(result).isNotNull();
		assertThat(result.getCommentId()).isEqualTo(1L);
		assertThat(result.getContent()).isEqualTo("테스트 댓글입니다.");
		assertThat(result.getMemberName()).isEqualTo("test");
	}
	
	@Test
	@DisplayName("Entity -> DTO 변환 실패 - Comment가 Null인 경우")
	void entityToDto_Failed_NullComment() {
		// given
		Comment comment = null;
		
		// when-then
		assertThatThrownBy(() -> commentMapper.entityToDto(comment))
			.isInstanceOf(NullPointerException.class);
	}
	
	@Test
	@DisplayName("Entity -> DTO 변환 실패 - Member가 Null인 경우")
	void entityToDto_Failed_NullMember() {
		// given
		Comment comment = Comment.builder()
			.commentId(1L)
			.content("테스트 댓글")
			.member(null)
			.post(PostEntity.builder().postId(1L).build())
			.build();
		
		// when-then
		assertThatThrownBy(() -> commentMapper.entityToDto(comment))
			.isInstanceOf(NullPointerException.class);
	}
}