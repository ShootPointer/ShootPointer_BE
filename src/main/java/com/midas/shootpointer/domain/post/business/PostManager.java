package com.midas.shootpointer.domain.post.business;

import com.midas.shootpointer.domain.member.entity.Member;
import com.midas.shootpointer.domain.post.entity.PostEntity;
import com.midas.shootpointer.domain.post.helper.PostHelper;
import com.midas.shootpointer.domain.post.repository.PostCommandRepository;
import com.midas.shootpointer.domain.post.repository.PostQueryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class PostManager {
    private final PostHelper postHelper;
    private final PostCommandRepository postCommandRepository;
    public Long save(Member member, PostEntity postEntity, UUID highlightId){
        /*
         * 1. Highlight URL이 유저의 영상으로 일치 여부.
         */
        postHelper.isValidateHighlightId(member,highlightId);
        /*
         * 2. 해시태그가 올바른 지 여부.
         */
        postHelper.isValidPostHashTag(postEntity.getHashTag());
        return postCommandRepository.save(postEntity).getPostId();
    }
}
