package com.midas.shootpointer.domain.like.helper;
import java.util.UUID;

public interface LikeValidation {
    void isValidCreateLike(UUID memberId,Long postId);
}
