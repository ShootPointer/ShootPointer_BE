package com.midas.shootpointer.domain.comment.controller;

import com.midas.shootpointer.domain.comment.business.command.CommentCommandService;
import com.midas.shootpointer.domain.comment.dto.request.CommentRequestDto;
import com.midas.shootpointer.domain.comment.entity.Comment;
import com.midas.shootpointer.domain.comment.mapper.CommentMapper;
import com.midas.shootpointer.domain.member.entity.Member;
import com.midas.shootpointer.domain.post.entity.PostEntity;
import com.midas.shootpointer.global.dto.ApiResponse;
import com.midas.shootpointer.global.security.SecurityUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/comment")
@RequiredArgsConstructor
public class CommentCommandController {
	
	private final CommentCommandService commentCommandService;
	private final CommentMapper commentMapper;
	
	@PostMapping
	public ResponseEntity<ApiResponse<Long>> create(@RequestBody CommentRequestDto requestDto) {
		
		Member member = SecurityUtils.getCurrentMember();
		PostEntity postEntity = PostEntity.builder()
			.postId(requestDto.getPostId())
			.build();
		Comment comment = commentMapper.dtoToEntity(requestDto, member, postEntity);
		
		return ResponseEntity.ok(ApiResponse.created(commentCommandService.create(comment)));
	}

}
