package com.midas.shootpointer.domain.like.helper;

import com.midas.shootpointer.domain.like.repository.LikeCommandRepository;
import com.midas.shootpointer.domain.post.entity.PostEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class LikeUtilImpl implements LikeUtil {
    @Transactional
    @Override
    public void increaseLikeCnt(PostEntity post) {

    }
    @Transactional
    @Override
    public void decreaseLikeCnt(PostEntity post) {

    }
}
