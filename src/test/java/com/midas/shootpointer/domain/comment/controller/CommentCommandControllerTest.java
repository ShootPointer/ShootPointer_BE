package com.midas.shootpointer.domain.comment.controller;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.midas.shootpointer.domain.comment.business.command.CommentCommandService;
import com.midas.shootpointer.domain.comment.dto.request.CommentRequestDto;
import com.midas.shootpointer.domain.comment.entity.Comment;
import com.midas.shootpointer.domain.comment.mapper.CommentMapper;
import com.midas.shootpointer.domain.member.entity.Member;
import com.midas.shootpointer.domain.post.entity.PostEntity;
import com.midas.shootpointer.domain.post.helper.PostHelper;
import com.midas.shootpointer.global.common.ErrorCode;
import com.midas.shootpointer.global.exception.CustomException;
import com.midas.shootpointer.global.security.SecurityUtils;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(CommentCommandController.class)
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
	
	@Test
	@DisplayName("댓글 생성 성공")
	void create_Success() throws Exception {
		// given
		CommentRequestDto requestDto = CommentRequestDto.builder()
			.postId(1L)
			.content("테스트 댓글입니다.")
			.build();
		
		Member member = createMember();
		PostEntity postEntity = createPostEntity();
		Comment comment = createComment();
		Long savedCommentId = 1L;
		
		try (MockedStatic<SecurityUtils> securityUtilsMock = Mockito.mockStatic(SecurityUtils.class)) {
			securityUtilsMock.when(SecurityUtils::getCurrentMember).thenReturn(member);
			given(postHelper.findPostByPostId(1L)).willReturn(postEntity);
			given(commentMapper.dtoToEntity(requestDto, member, postEntity)).willReturn(comment);
			given(commentCommandService.create(comment)).willReturn(savedCommentId);
			
			// when-then
			mockMvc.perform(post("/api/comment")
					.contentType(MediaType.APPLICATION_JSON)
					.content(objectMapper.writeValueAsString(requestDto)))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.success").value(true))
				.andExpect(jsonPath("$.data").value(1L));
			
			then(postHelper).should().findPostByPostId(1L);
			then(commentMapper).should().dtoToEntity(requestDto, member, postEntity);
			then(commentCommandService).should().create(comment);
		}
	}
	
	@Test
	@DisplayName("댓글 생성 실패 - 존재하지 않는 게시물")
	void create_PostNotFound() throws Exception {
		// given
		CommentRequestDto requestDto = CommentRequestDto.builder()
			.postId(999L)
			.content("테스트 댓글입니다.")
			.build();
		
		Member member = createMember();
		
		try (MockedStatic<SecurityUtils> securityUtilsMock = Mockito.mockStatic(SecurityUtils.class)) {
			securityUtilsMock.when(SecurityUtils::getCurrentMember).thenReturn(member);
			given(postHelper.findPostByPostId(999L))
				.willThrow(new CustomException(ErrorCode.IS_NOT_EXIST_POST));
			
			// when-then
			mockMvc.perform(post("/api/comment")
					.contentType(MediaType.APPLICATION_JSON)
					.content(objectMapper.writeValueAsString(requestDto)))
				.andExpect(status().isBadRequest());
			
			then(postHelper).should().findPostByPostId(999L);
		}
	}
	
	@Test
	@DisplayName("댓글 생성 실패 - 빈 내용")
	void create_EmptyContent() throws Exception {
		// given
		CommentRequestDto requestDto = CommentRequestDto.builder()
			.postId(1L)
			.content("")
			.build();
		
		// when-then
		mockMvc.perform(post("/api/comment")
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(requestDto)))
			.andExpect(status().isBadRequest());
	}
	
	@Test
	@DisplayName("댓글 생성 실패 - null 내용")
	void create_NullContent() throws Exception {
		// given
		CommentRequestDto requestDto = CommentRequestDto.builder()
			.postId(1L)
			.content(null)
			.build();
		
		// when-then
		mockMvc.perform(post("/api/comment")
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(requestDto)))
			.andExpect(status().isBadRequest());
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