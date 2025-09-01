package com.midas.shootpointer.domain.like.helper;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class LikeHelperImpl implements LikeHelper {
    private final LikeValidation likeValidation;
    @Override
    public void isValidCreateLike(UUID memberId, Long postId) {
        likeValidation.isValidCreateLike(memberId,postId);
    }

    @Override
    public void isValidDeleteLike(UUID memberId, Long postId) {

    }
}
