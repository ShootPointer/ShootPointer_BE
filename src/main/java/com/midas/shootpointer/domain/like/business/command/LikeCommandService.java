package com.midas.shootpointer.domain.like.business.command;

import com.midas.shootpointer.domain.member.entity.Member;
import org.springframework.stereotype.Service;

@Service
public interface LikeCommandService {
    Long create(Long postId, Long memberId);
    Long delete(Long postId,Long memberId);
}
