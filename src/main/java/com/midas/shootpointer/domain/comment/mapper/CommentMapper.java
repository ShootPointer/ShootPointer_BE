package com.midas.shootpointer.domain.comment.mapper;

import com.midas.shootpointer.domain.comment.dto.request.CommentRequestDto;
import com.midas.shootpointer.domain.comment.entity.Comment;
import com.midas.shootpointer.domain.member.entity.Member;
import com.midas.shootpointer.domain.post.entity.PostEntity;

public interface CommentMapper {
	
	Comment dtoToEntity(CommentRequestDto commentRequestDto, Member member, PostEntity post);
}
