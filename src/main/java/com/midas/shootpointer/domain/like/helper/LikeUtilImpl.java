package com.midas.shootpointer.domain.like.helper;

import com.midas.shootpointer.domain.like.entity.LikeEntity;
import com.midas.shootpointer.domain.like.repository.LikeCommandRepository;
import com.midas.shootpointer.domain.like.repository.LikeQueryRepository;
import com.midas.shootpointer.domain.member.entity.Member;
import com.midas.shootpointer.domain.post.entity.PostEntity;
import com.midas.shootpointer.domain.post.repository.PostCommandRepository;
import com.midas.shootpointer.global.common.ErrorCode;
import com.midas.shootpointer.global.exception.CustomException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class LikeUtilImpl implements LikeUtil {
    private final PostCommandRepository postCommandRepository;
    private final LikeQueryRepository likeQueryRepository;
    private final LikeCommandRepository likeCommandRepository;

    @Override
    public void increaseLikeCnt(PostEntity post) {
        post.createLike();
        postCommandRepository.save(post);
    }

    @Override
    public void decreaseLikeCnt(PostEntity post) {
        post.deleteLike();
        postCommandRepository.save(post);
    }

    @Override
    public LikeEntity createLike(PostEntity post, Member member) {
        LikeEntity likeEntity=LikeEntity.builder()
                .member(member)
                .post(post)
                .build();
        return likeCommandRepository.save(likeEntity);
    }

    @Override
    public void deleteLike(LikeEntity like) {
        likeCommandRepository.delete(like);
    }

    @Override
    public LikeEntity findByPostIdAndMemberId(UUID memberId, Long postId) {
        return likeQueryRepository.findByPostIdAndMemberId(postId,memberId)
                .orElseThrow(()-> new CustomException(ErrorCode.NOT_FOUND_LIKE));
    }
}
