package com.midas.shootpointer.domain.like.business.command;

import com.midas.shootpointer.domain.member.entity.Member;
import org.springframework.stereotype.Service;

public interface LikeCommandService {
    Long create(Long postId, Member member);
    Long delete(Long postId,Member member);
}
