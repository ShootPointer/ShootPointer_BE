package com.midas.shootpointer.domain.like.business;

import com.midas.shootpointer.domain.like.entity.LikeEntity;
import com.midas.shootpointer.domain.like.helper.LikeHelper;
import com.midas.shootpointer.domain.member.entity.Member;
import com.midas.shootpointer.domain.post.entity.PostEntity;
import com.midas.shootpointer.domain.post.helper.PostHelper;
import com.midas.shootpointer.global.annotation.DistributedLock;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class LikeManager {
    private final LikeHelper likeHelper;
    private final PostHelper postHelper;

    @Transactional
    public Long increase(Long postId, Member member){
        /**
         * 1. 게시물이 존재하는 지 여부
         */
        PostEntity postEntity=postHelper.findByPostByPostIdWithPessimisticLock(postId);


        /**
         * 2. 좋아요 유효성 검증 - 사용자가 이전에 좋아요를 누르지 않았는지 확인.
         */
        likeHelper.isValidCreateLike(member.getMemberId(),postId);


        /**
         * 3. 좋아요 생성 및 증가.
         */
        likeHelper.increaseLikeCnt(postEntity);
        LikeEntity savedLike=likeHelper.createLike(postEntity,member);

        return savedLike.getLikeId();
    }

    @Transactional
    public Long decrease(Long postId,Member member){
        /**
         * 1. 게시물이 존재하는 지 여부
         */
        PostEntity postEntity=postHelper.findByPostByPostIdWithPessimisticLock(postId);

        /**
         * 2. 좋아요 엔티티 가져오기.
         */
        LikeEntity likeEntity=likeHelper.findByPostIdAndMemberId(member.getMemberId(),postId);

        /**
         * 3. 좋아요 감소.
         */
        likeHelper.decreaseLikeCnt(postEntity);
        likeHelper.deleteLike(likeEntity);

        return likeEntity.getLikeId();
    }
}
