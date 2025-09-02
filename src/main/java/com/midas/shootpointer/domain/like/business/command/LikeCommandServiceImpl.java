package com.midas.shootpointer.domain.like.business.command;

import com.midas.shootpointer.domain.like.business.LikeManager;
import com.midas.shootpointer.domain.like.helper.LikeValidation;
import com.midas.shootpointer.domain.member.entity.Member;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class LikeCommandServiceImpl implements LikeCommandService {
    private final LikeManager likeManager;


    @Override
    public Long create(Long postId, Member member) {
        return likeManager.increase(postId, member);
    }

    @Override
    public Long delete(Long postId, Member member) {
        return likeManager.decrease(postId,member);
    }
}
