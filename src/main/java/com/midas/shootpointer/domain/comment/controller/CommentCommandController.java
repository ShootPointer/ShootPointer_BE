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
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/comment")
@RequiredArgsConstructor
@Tag(name = "댓글", description = "댓글 API")
public class CommentCommandController {
	
	private final CommentCommandService commentCommandService;
	private final CommentMapper commentMapper;
	private final PostHelper postHelper;
	
	@Operation(summary = "댓글 등록 API - [담당자 : 박재성]",
		responses = {
			@io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "댓글 등록 성공",
			content = @Content(mediaType = "application/json",
			schema =  @Schema(implementation = com.midas.shootpointer.global.dto.ApiResponse.class))),
			@io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "4XX", description = "댓글 등록 실패",
			content = @Content(mediaType = "application/json",
			schema = @Schema(implementation = com.midas.shootpointer.global.dto.ApiResponse.class)))
		}
	)
	@PostMapping
	public ResponseEntity<ApiResponse<Long>> create(@RequestBody CommentRequestDto requestDto) {
		
		Member member = SecurityUtils.getCurrentMember();
		
		PostEntity postEntity = postHelper.findPostByPostId(requestDto.getPostId());
		Comment comment = commentMapper.dtoToEntity(requestDto, member, postEntity);
		
		return ResponseEntity.ok(ApiResponse.created(commentCommandService.create(comment)));
	}
	
	@Operation(
		summary = "댓글 삭제 API - [담당자 : 박재성]",
		responses = {
			@io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "204", description = "댓글 삭제 성공"),
			@io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "4XX", description = "댓글 삭제 실패",
				content = @Content(mediaType = "application/json",
					schema = @Schema(implementation = com.midas.shootpointer.global.dto.ApiResponse.class)))
		}
	)
	@DeleteMapping("/{commentId}")
	public ResponseEntity<ApiResponse<Void>> delete(@PathVariable Long commentId) {
		
		Member member = SecurityUtils.getCurrentMember();
		commentCommandService.delete(commentId, member.getMemberId());
		
		return ResponseEntity.noContent().build();
	}
	
	@Operation(
		summary = "댓글 수정 API - [담당자 : 박재성]",
		responses = {
			@io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "댓글 수정 성공",
				content = @Content(mediaType = "application/json",
					schema = @Schema(implementation = com.midas.shootpointer.global.dto.ApiResponse.class))),
			@io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "4XX", description = "댓글 수정 실패",
				content = @Content(mediaType = "application/json",
					schema = @Schema(implementation = com.midas.shootpointer.global.dto.ApiResponse.class)))
		}
	)
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
