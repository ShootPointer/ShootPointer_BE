package com.midas.shootpointer.domain.comment.mapper;

import com.midas.shootpointer.domain.comment.dto.request.CommentRequestDto;
import com.midas.shootpointer.domain.comment.dto.response.CommentResponseDto;
import com.midas.shootpointer.domain.comment.entity.Comment;
import com.midas.shootpointer.domain.member.entity.Member;
import com.midas.shootpointer.domain.post.entity.PostEntity;
import org.springframework.stereotype.Component;

@Component
public class CommentMapperImpl implements CommentMapper {
	
	@Override
	public Comment dtoToEntity(CommentRequestDto commentRequestDto, Member member,
		PostEntity post) {
		return Comment.builder()
			.content(commentRequestDto.getContent())
			.member(member)
			.post(post)
			.build();
	}
	
	@Override
	public CommentResponseDto entityToDto(Comment comment) {
		return CommentResponseDto.builder()
			.commentId(comment.getCommentId())
			.content(comment.getContent())
			.writerName(comment.getMember().getUsername())
			.createdAt(comment.getCreatedAt())
			.build();
	}
}
