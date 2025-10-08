package com.midas.shootpointer.domain.comment.controller;

import com.midas.shootpointer.domain.comment.business.query.CommentQueryService;
import com.midas.shootpointer.domain.comment.dto.response.CommentResponseDto;
import com.midas.shootpointer.domain.comment.entity.Comment;
import com.midas.shootpointer.domain.comment.mapper.CommentMapper;
import com.midas.shootpointer.global.dto.ApiResponse;
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
public class CommentQueryController {
	
	private final CommentQueryService commentQueryService;
	private final CommentMapper commentMapper;
	
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
