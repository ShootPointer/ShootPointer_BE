package com.midas.shootpointer.domain.comment.controller;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.willDoNothing;
import static org.mockito.BDDMockito.willThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.midas.shootpointer.WithMockCustomMember;
import com.midas.shootpointer.domain.comment.business.command.CommentCommandService;
import com.midas.shootpointer.domain.comment.dto.request.CommentRequestDto;
import com.midas.shootpointer.domain.comment.entity.Comment;
import com.midas.shootpointer.domain.comment.mapper.CommentMapper;
import com.midas.shootpointer.domain.member.entity.Member;
import com.midas.shootpointer.domain.post.entity.PostEntity;
import com.midas.shootpointer.domain.post.helper.PostHelper;
import com.midas.shootpointer.global.common.ErrorCode;
import com.midas.shootpointer.global.exception.CustomException;
import java.util.UUID;
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

@SpringBootTest
@DisplayName("CommentCommandController 테스트")
@AutoConfigureMockMvc
@ActiveProfiles("dev")
class CommentCommandControllerTest {
	
	@Autowired
	private MockMvc mockMvc;
	
	@Autowired
	private ObjectMapper objectMapper;
	
	@MockitoBean
	private CommentCommandService commentCommandService;
	
	@MockitoBean
	private CommentMapper commentMapper;
	
	@MockitoBean
	private PostHelper postHelper;
	
	private final String baseUrl = "/api/comment";
	
	@BeforeEach
	void setUp() {
		objectMapper = new ObjectMapper();
	}
	
	@Test
	@DisplayName("댓글 생성 성공")
	@WithMockCustomMember
	void create_Success() throws Exception {
		// given
		CommentRequestDto requestDto = CommentRequestDto.builder()
			.postId(1L)
			.content("테스트 댓글입니다.")
			.build();
		
		PostEntity postEntity = createPostEntity();
		Comment comment = createComment();
		Long savedCommentId = 1L;
		
		// when
		when(postHelper.findPostByPostId(1L)).thenReturn(postEntity);
		when(commentMapper.dtoToEntity(any(CommentRequestDto.class), any(Member.class), any(PostEntity.class)))
			.thenReturn(comment);
		when(commentCommandService.create(any(Comment.class))).thenReturn(savedCommentId);
		
		// then
		mockMvc.perform(post(baseUrl)
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(requestDto)))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.success").value(true))
			.andExpect(jsonPath("$.data").value(1L))
			.andDo(print());
		
		verify(postHelper, times(1)).findPostByPostId(1L);
		verify(commentMapper, times(1)).dtoToEntity(any(CommentRequestDto.class), any(Member.class), any(PostEntity.class));
		verify(commentCommandService, times(1)).create(any(Comment.class));
	}
	
	@Test
	@DisplayName("댓글 생성 실패 - 존재하지 않는 게시물")
	@WithMockCustomMember
	void create_PostNotFound() throws Exception {
		// given
		CommentRequestDto requestDto = CommentRequestDto.builder()
			.postId(999L)
			.content("테스트 댓글입니다.")
			.build();
		
		// when
		when(postHelper.findPostByPostId(999L))
			.thenThrow(new CustomException(ErrorCode.IS_NOT_EXIST_POST));
		
		// then
		mockMvc.perform(post(baseUrl)
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(requestDto)))
			.andExpect(status().is2xxSuccessful())
			.andDo(print());
		
		verify(postHelper, times(1)).findPostByPostId(999L);
	}
	
	@Test
	@DisplayName("댓글 생성 실패 - 빈 내용")
	@WithMockCustomMember
	void create_EmptyContent() throws Exception {
		// given
		CommentRequestDto requestDto = CommentRequestDto.builder()
			.postId(1L)
			.content("")
			.build();
		
		// when-then
		mockMvc.perform(post(baseUrl)
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(requestDto)))
			.andExpect(status().is2xxSuccessful())
			.andDo(print());
	}
	
	@Test
	@DisplayName("댓글 생성 실패 - null 내용")
	@WithMockCustomMember
	void create_NullContent() throws Exception {
		// given
		CommentRequestDto requestDto = CommentRequestDto.builder()
			.postId(1L)
			.content(null)
			.build();
		
		// when-then
		mockMvc.perform(post(baseUrl)
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(requestDto)))
			.andExpect(status().is2xxSuccessful())
			.andDo(print());
	}
	
	
	@Test
	@DisplayName("댓글 삭제 성공")
	@WithMockCustomMember
	void delete_Success() throws Exception {
		// given
		Long commentId = 1L;
		
		willDoNothing().given(commentCommandService).delete(commentId, UUID.fromString("00000000-0000-0000-0000-000000000000"));
		
		// when-then
		mockMvc.perform(delete(baseUrl + "/" + commentId))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.success").value(true))
			.andDo(print());
	}
	
	@Test
	@DisplayName("댓글 삭제 실패 - 존재하지 않는 댓글")
	@WithMockCustomMember
	void delete_Failed_CommentNotFound() throws Exception {
		// given
		Long commentId = 999L;
		
		willThrow(new CustomException(ErrorCode.IS_NOT_EXIST_COMMENT))
			.given(commentCommandService).delete(eq(commentId), any(UUID.class));
		
		// when-then
		mockMvc.perform(delete(baseUrl + "/" + commentId))
			.andExpect(status().is2xxSuccessful())
			.andDo(print());
		
		verify(commentCommandService, times(1)).delete(eq(commentId), any(UUID.class));
	}
	
	@Test
	@DisplayName("댓글 삭제 실패 - 권한 없음 (작성자가 아님)")
	@WithMockCustomMember
	void delete_Failed_Forbidden() throws Exception {
		// given
		Long commentId = 1L;
		
		willThrow(new CustomException(ErrorCode.FORBIDDEN_COMMENT_DELETE))
			.given(commentCommandService).delete(eq(commentId), any(UUID.class));
		
		// when-then
		mockMvc.perform(delete(baseUrl + "/" + commentId))
			.andExpect(status().is2xxSuccessful())
			.andDo(print());
		
		verify(commentCommandService, times(1)).delete(eq(commentId), any(UUID.class));
	}
	
	@Test
	@DisplayName("여러 댓글 순차적 삭제 성공")
	@WithMockCustomMember
	void delete_Multiple_Success() throws Exception {
		// given
		Long commentId1 = 1L;
		Long commentId2 = 2L;
		Long commentId3 = 3L;
		
		willDoNothing().given(commentCommandService).delete(eq(commentId1), any(UUID.class));
		willDoNothing().given(commentCommandService).delete(eq(commentId2), any(UUID.class));
		willDoNothing().given(commentCommandService).delete(eq(commentId3), any(UUID.class));
		
		// when-then
		mockMvc.perform(delete(baseUrl + "/" + commentId1))
			.andExpect(status().isOk())
			.andDo(print());
		
		mockMvc.perform(delete(baseUrl + "/" + commentId2))
			.andExpect(status().isOk())
			.andDo(print());
		
		mockMvc.perform(delete(baseUrl + "/" + commentId3))
			.andExpect(status().isOk())
			.andDo(print());
		
		verify(commentCommandService, times(1)).delete(eq(commentId1), any(UUID.class));
		verify(commentCommandService, times(1)).delete(eq(commentId2), any(UUID.class));
		verify(commentCommandService, times(1)).delete(eq(commentId3), any(UUID.class));
	}
	
	private Member createMember() {
		return Member.builder()
			.memberId(java.util.UUID.randomUUID())
			.email("test@naver.com")
			.username("test")
			.build();
	}
	
	private PostEntity createPostEntity() {
		return PostEntity.builder()
			.postId(1L)
			.build();
	}
	
	private Comment createComment() {
		return Comment.builder()
			.content("테스트입니다.")
			.member(createMember())
			.post(createPostEntity())
			.build();
	}
}