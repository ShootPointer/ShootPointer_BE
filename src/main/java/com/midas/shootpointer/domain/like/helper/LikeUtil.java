package com.midas.shootpointer.domain.like.helper;

import com.midas.shootpointer.domain.like.entity.LikeEntity;
import com.midas.shootpointer.domain.member.entity.Member;
import com.midas.shootpointer.domain.post.entity.PostEntity;

import java.util.UUID;

public interface LikeUtil {
    void increaseLikeCnt(Long postId);
    void decreaseLikeCnt(PostEntity post);
    LikeEntity createLike(PostEntity post, Member member);
    void deleteLike(LikeEntity like);
    LikeEntity findByPostIdAndMemberId(UUID memberId, Long postId);
}
