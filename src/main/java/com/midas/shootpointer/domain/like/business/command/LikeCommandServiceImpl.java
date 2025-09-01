package com.midas.shootpointer.domain.like.business.command;

import com.midas.shootpointer.domain.like.helper.LikeValidation;
import com.midas.shootpointer.domain.member.entity.Member;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class LikeCommandServiceImpl implements LikeCommandService{
    private final LikeValidation likeValidation;


    @Override
    public Long create(Long postId, Long memberId) {
        return 0L;
    }

    @Override
    public Long delete(Long postId, Long memberId) {
        return 0L;
    }
}
