package com.midas.shootpointer.domain.like.helper;

import com.midas.shootpointer.domain.like.entity.LikeEntity;
import com.midas.shootpointer.domain.member.entity.Member;
import com.midas.shootpointer.domain.post.entity.PostEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class LikeHelperImpl implements LikeHelper {
    private final LikeValidation likeValidation;
    private final LikeUtil likeUtil;
    @Override
    public void isValidCreateLike(UUID memberId, Long postId) {
        likeValidation.isValidCreateLike(memberId,postId);
    }


    @Override
    public void increaseLikeCnt(PostEntity post) {
        likeUtil.increaseLikeCnt(post);
    }

    @Override
    public void decreaseLikeCnt(PostEntity post) {
        likeUtil.decreaseLikeCnt(post);
    }

    @Override
    public LikeEntity createLike(PostEntity post, Member member) {
        return likeUtil.createLike(post,member);
    }

    @Override
    public void deleteLike(LikeEntity like) {
        likeUtil.deleteLike(like);
    }

    @Override
    public LikeEntity findByPostIdAndMemberId(UUID memberId, Long postId) {
        return likeUtil.findByPostIdAndMemberId(memberId,postId);
    }
}
