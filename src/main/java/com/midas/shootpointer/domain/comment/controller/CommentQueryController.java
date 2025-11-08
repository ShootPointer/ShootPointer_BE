package com.midas.shootpointer.domain.comment.controller;

import com.midas.shootpointer.domain.comment.business.query.CommentQueryService;
import com.midas.shootpointer.domain.comment.dto.response.CommentResponseDto;
import com.midas.shootpointer.domain.comment.entity.Comment;
import com.midas.shootpointer.domain.comment.mapper.CommentMapper;
import com.midas.shootpointer.global.dto.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/comment")
@RequiredArgsConstructor
@Tag(name = "댓글", description = "댓글 API")
public class CommentQueryController {
	
	private final CommentQueryService commentQueryService;
	private final CommentMapper commentMapper;
	
	@Operation(
		summary = "게시글별 댓글 조회 API - [담당자 : 박재성]",
		responses = {
			@io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "댓글 조회 성공",
				content = @Content(mediaType = "application/json",
					schema = @Schema(implementation = com.midas.shootpointer.global.dto.ApiResponse.class))),
			@io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "4XX", description = "댓글 조회 실패",
				content = @Content(mediaType = "application/json",
					schema = @Schema(implementation = com.midas.shootpointer.global.dto.ApiResponse.class)))
		}
	)
	@GetMapping("/{postId}")
	public ResponseEntity<ApiResponse<List<CommentResponseDto>>>
	getCommentsByPostId(@PathVariable Long postId) {
		List<Comment> comments = commentQueryService.getCommentsByPostId(postId); // List로 댓글들 다 불러오기(최신순)
		
		List<CommentResponseDto> responseDto = comments.stream()
			.map(commentMapper::entityToDto)
			.toList();
		
		return ResponseEntity.ok(ApiResponse.ok(responseDto));
	}
	
}
