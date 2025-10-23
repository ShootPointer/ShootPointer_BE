package com.midas.shootpointer.domain.comment.controller;

import com.midas.shootpointer.domain.comment.business.command.CommentCommandService;
import com.midas.shootpointer.domain.comment.dto.request.CommentRequestDto;
import com.midas.shootpointer.domain.comment.dto.request.CommentUpdateRequestDto;
import com.midas.shootpointer.domain.comment.dto.response.CommentResponseDto;
import com.midas.shootpointer.domain.comment.entity.Comment;
import com.midas.shootpointer.domain.comment.mapper.CommentMapper;
import com.midas.shootpointer.domain.member.entity.Member;
import com.midas.shootpointer.domain.post.entity.PostEntity;
import com.midas.shootpointer.domain.post.helper.simple.PostHelper;
import com.midas.shootpointer.global.dto.ApiResponse;
import com.midas.shootpointer.global.security.SecurityUtils;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/comment")
@RequiredArgsConstructor
public class CommentCommandController {
	
	private final CommentCommandService commentCommandService;
	private final CommentMapper commentMapper;
	private final PostHelper postHelper;
	
	@PostMapping
	public ResponseEntity<ApiResponse<Long>> create(@RequestBody CommentRequestDto requestDto) {
		
		Member member = SecurityUtils.getCurrentMember();
		
		PostEntity postEntity = postHelper.findPostByPostId(requestDto.getPostId());
		Comment comment = commentMapper.dtoToEntity(requestDto, member, postEntity);
		
		return ResponseEntity.ok(ApiResponse.created(commentCommandService.create(comment)));
	}
	
	@DeleteMapping("/{commentId}")
	public ResponseEntity<ApiResponse<Void>> delete(@PathVariable Long commentId) {
		
		Member member = SecurityUtils.getCurrentMember();
		commentCommandService.delete(commentId, member.getMemberId());
		
		return ResponseEntity.noContent().build();
	}
	
	@PatchMapping("/{commentId}")
	public ResponseEntity<ApiResponse<CommentResponseDto>> update(@PathVariable Long commentId,
		@Valid @RequestBody CommentUpdateRequestDto updateRequestDto) {
		
		Member member = SecurityUtils.getCurrentMember();
		Comment updatedComment = commentCommandService.update(commentId, updateRequestDto.getContent(),
			member.getMemberId());
		
		CommentResponseDto responseDto = commentMapper.entityToDto(updatedComment);
		
		return ResponseEntity.ok(ApiResponse.ok(responseDto));
	}
}
