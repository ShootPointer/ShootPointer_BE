package com.midas.shootpointer.domain.like.helper;

import com.midas.shootpointer.domain.like.entity.LikeEntity;
import com.midas.shootpointer.domain.member.entity.Member;
import com.midas.shootpointer.domain.post.entity.PostEntity;

import java.util.UUID;

public interface LikeValidation {
    void isValidCreateLike(UUID memberId,Long postId);
    void isValidDeleteLike(LikeEntity like, PostEntity post, Member member);
}
