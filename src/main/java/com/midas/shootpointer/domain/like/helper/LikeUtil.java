package com.midas.shootpointer.domain.like.helper;

import com.midas.shootpointer.domain.post.entity.PostEntity;

public interface LikeUtil {
    void increaseLikeCnt(PostEntity post);
    void decreaseLikeCnt(PostEntity post);
}
