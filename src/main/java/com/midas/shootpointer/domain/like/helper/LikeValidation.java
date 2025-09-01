package com.midas.shootpointer.domain.like.helper;

import com.midas.shootpointer.domain.member.entity.Member;

import java.util.UUID;

public interface LikeValidation {
    void isValidCreateLike(UUID memberId,Long postId);
    void isValidDeleteLike(UUID memberId,Long postId);
}
