package com.midas.shootpointer.domain.post.business;

import com.midas.shootpointer.domain.highlight.entity.HighlightEntity;
import com.midas.shootpointer.domain.highlight.helper.HighlightHelper;
import com.midas.shootpointer.domain.member.entity.Member;
import com.midas.shootpointer.domain.post.dto.PostRequest;
import com.midas.shootpointer.domain.post.entity.PostEntity;
import com.midas.shootpointer.domain.post.helper.PostHelper;
import com.midas.shootpointer.domain.post.repository.PostCommandRepository;
import com.midas.shootpointer.domain.post.repository.PostQueryRepository;
import com.midas.shootpointer.global.common.ErrorCode;
import com.midas.shootpointer.global.exception.CustomException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class PostManager {
    private final PostHelper postHelper;
    private final HighlightHelper highlightHelper;
    private final PostCommandRepository postCommandRepository;
    private final PostQueryRepository postQueryRepository;

    public Long save(Member member, PostEntity postEntity, UUID highlightId){
        /**
         * 1.하이라이트 영상 불러오기.
         */
        HighlightEntity highlightEntity=highlightHelper.findHighlightByHighlightId(highlightId);

        /*
         * 2. Highlight URL이 유저의 영상으로 일치 여부.
         */
        postHelper.isValidateHighlightId(member,highlightId);

        /*
         * 3. 해시태그가 올바른 지 여부.
         */
        postHelper.isValidPostHashTag(postEntity.getHashTag());

        /**
         * 4. 하이라이트 저장.
         */
        postEntity.setHighlight(highlightEntity);

        return postCommandRepository.save(postEntity).getPostId();
    }

    public Long update(PostRequest request,Member member,Long postId){
        /**
         * 1. 게시물이 존재하는 지 여부
         */
        PostEntity postEntity=postQueryRepository.findByPostId(postId)
                .orElseThrow(()->new CustomException(ErrorCode.IS_NOT_EXIST_POST));

        /**
         * 2. 게시물의 삭제 여부
         */
        postHelper.isDeleted(postEntity);

        /**
         * 3. 게시물이 멤버의 게시물인지 확인
         */
        postHelper.isMembersPost(postEntity,member);

        /**
         * 4.하이라이트 영상 불러오기.
         */
        HighlightEntity highlightEntity=highlightHelper.findHighlightByHighlightId(request.getHighlightId());

        /**
         * 5. Highlight URL이 유저의 영상으로 일치 여부.
         */
        postHelper.isValidateHighlightId(member,request.getHighlightId());

        /**
         * 6. 해시태그가 올바른 지 여부.
         */
        postHelper.isValidPostHashTag(request.getHashTag());

        /**
         * 7. 수정 진행
         */
        postEntity.update(
                request.getTitle(),
                request.getContent(),
                request.getHashTag(),
                highlightEntity
        );
        postEntity=postCommandRepository.save(postEntity);

        return postEntity.getPostId();
    }
}
