package com.midas.shootpointer.domain.comment.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.midas.shootpointer.WithMockCustomMember;
import com.midas.shootpointer.domain.comment.business.query.CommentQueryService;
import com.midas.shootpointer.domain.comment.dto.response.CommentResponseDto;
import com.midas.shootpointer.domain.comment.entity.Comment;
import com.midas.shootpointer.domain.comment.mapper.CommentMapper;
import com.midas.shootpointer.domain.member.entity.Member;
import com.midas.shootpointer.domain.post.entity.PostEntity;
import com.midas.shootpointer.global.common.ErrorCode;
import com.midas.shootpointer.global.exception.CustomException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@DisplayName("CommentQueryController 테스트")
@AutoConfigureMockMvc
@ActiveProfiles("dev")
class CommentQueryControllerTest {
	
	@Autowired
	private MockMvc mockMvc;
	
	@Autowired
	private ObjectMapper objectMapper;
	
	@MockitoBean
	private CommentQueryService commentQueryService;
	
	@MockitoBean
	private CommentMapper commentMapper;
	
	private final String baseUrl = "/api/comment/";
	
	@BeforeEach
	void setUp() {
		objectMapper = new ObjectMapper();
	}
	@WithMockCustomMember
	@Test
	@DisplayName("게시물 ID로 댓글 목록 조회 성공")
	void getCommentsByPostId_Success() throws Exception {
		// given
		Long postId = 1L;
		PostEntity post = createPostEntity(postId);
		
		List<Comment> comments = Arrays.asList(
			createCommentWithId(3L, "최신 댓글", post),
			createCommentWithId(2L, "중간 댓글", post),
			createCommentWithId(1L, "오래된 댓글", post)
		);
		
		List<CommentResponseDto> responseDtos = Arrays.asList(
			createResponseDto(3L, "최신 댓글", "테스트유저1"),
			createResponseDto(2L, "중간 댓글", "테스트유저2"),
			createResponseDto(1L, "오래된 댓글", "테스트유저3")
		);
		
		// when
		when(commentQueryService.getCommentsByPostId(postId)).thenReturn(comments);
		when(commentMapper.entityToDto(comments.get(0))).thenReturn(responseDtos.get(0));
		when(commentMapper.entityToDto(comments.get(1))).thenReturn(responseDtos.get(1));
		when(commentMapper.entityToDto(comments.get(2))).thenReturn(responseDtos.get(2));
		
		// then
		mockMvc.perform(get(baseUrl + "/" + postId)
				.contentType(MediaType.APPLICATION_JSON))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.success").value(true))
			.andExpect(jsonPath("$.data").isArray())
			.andExpect(jsonPath("$.data.length()").value(3))
			.andExpect(jsonPath("$.data[0].commentId").value(3L))
			.andExpect(jsonPath("$.data[0].content").value("최신 댓글"))
			.andExpect(jsonPath("$.data[0].memberName").value("테스트유저1"))
			.andExpect(jsonPath("$.data[1].commentId").value(2L))
			.andExpect(jsonPath("$.data[2].commentId").value(1L))
			.andDo(print());
		
		verify(commentQueryService, times(1)).getCommentsByPostId(postId);
		verify(commentMapper, times(3)).entityToDto(any(Comment.class));
	}
	@WithMockCustomMember
	@Test
	@DisplayName("댓글 목록 조회 성공 - 빈 리스트")
	void getCommentsByPostId_Success_EmptyList() throws Exception {
		// given
		Long postId = 1L;
		
		// when
		when(commentQueryService.getCommentsByPostId(postId)).thenReturn(List.of());
		
		// then
		mockMvc.perform(get(baseUrl + "/" + postId)
				.contentType(MediaType.APPLICATION_JSON))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.success").value(true))
			.andExpect(jsonPath("$.data").isArray())
			.andExpect(jsonPath("$.data.length()").value(0))
			.andDo(print());
		
		verify(commentQueryService, times(1)).getCommentsByPostId(postId);
		verify(commentMapper, times(0)).entityToDto(any(Comment.class));
	}
	@WithMockCustomMember
	@Test
	@DisplayName("댓글 목록 조회 실패 - 존재하지 않는 게시물")
	void getCommentsByPostId_Failed_PostNotFound() throws Exception {
		// given
		Long postId = 999L;
		
		// when
		when(commentQueryService.getCommentsByPostId(postId))
			.thenThrow(new CustomException(ErrorCode.IS_NOT_EXIST_POST));
		
		// then
		mockMvc.perform(get(baseUrl + "/" + postId)
				.contentType(MediaType.APPLICATION_JSON))
			.andExpect(status().is2xxSuccessful())
			.andDo(print());
		
		verify(commentQueryService, times(1)).getCommentsByPostId(postId);
	}
	
	@Test
	@DisplayName("댓글 목록 조회 실패 - 잘못된 게시물 ID 형식")
	@WithMockCustomMember
	void getCommentsByPostId_Failed_InvalidPostIdFormat() throws Exception {
		// given
		String invalidPostId = "invalid";
		
		// when-then
		mockMvc.perform(get(baseUrl + "/" + invalidPostId)
				.contentType(MediaType.APPLICATION_JSON))
			.andExpect(status().is2xxSuccessful())
			.andDo(print());
	}
	@WithMockCustomMember
	@Test
	@DisplayName("단일 댓글 조회 성공")
	void getCommentsByPostId_Success_SingleComment() throws Exception {
		// given
		Long postId = 1L;
		PostEntity post = createPostEntity(postId);
		
		List<Comment> comments = List.of(
			createCommentWithId(1L, "단일 댓글입니다.", post)
		);
		
		List<CommentResponseDto> responseDtos = List.of(
			createResponseDto(1L, "단일 댓글입니다.", "test")
		);
		
		// when
		when(commentQueryService.getCommentsByPostId(postId)).thenReturn(comments);
		when(commentMapper.entityToDto(comments.get(0))).thenReturn(responseDtos.get(0));
		
		// then
		mockMvc.perform(get(baseUrl + "/" + postId)
				.contentType(MediaType.APPLICATION_JSON))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.success").value(true))
			.andExpect(jsonPath("$.data").isArray())
			.andExpect(jsonPath("$.data.length()").value(1))
			.andExpect(jsonPath("$.data[0].commentId").value(1L))
			.andExpect(jsonPath("$.data[0].content").value("단일 댓글입니다."))
			.andExpect(jsonPath("$.data[0].memberName").value("test"))
			.andDo(print());
		
		verify(commentQueryService, times(1)).getCommentsByPostId(postId);
		verify(commentMapper, times(1)).entityToDto(comments.get(0));
	}
	
	private Member createMember() {
		return Member.builder()
			.memberId(java.util.UUID.randomUUID())
			.email("test@naver.com")
			.username("test")
			.build();
	}
	
	private PostEntity createPostEntity(Long postId) {
		return PostEntity.builder()
			.postId(postId)
			.build();
	}
	
	private Comment createCommentWithId(Long commentId, String content, PostEntity post) {
		return Comment.builder()
			.commentId(commentId)
			.content(content)
			.member(createMember())
			.post(post)
			.build();
	}
	
	private CommentResponseDto createResponseDto(Long commentId, String content, String writerName) {
		return CommentResponseDto.builder()
			.commentId(commentId)
			.content(content)
			.memberName(writerName)
			.createdAt(LocalDateTime.now())
			.build();
	}
}