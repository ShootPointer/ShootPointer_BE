package com.midas.shootpointer.domain.post.helper;

import com.midas.shootpointer.domain.highlight.entity.HighlightEntity;
import com.midas.shootpointer.domain.member.entity.Member;
import com.midas.shootpointer.domain.post.dto.PostRequest;
import com.midas.shootpointer.domain.post.dto.PostResponse;
import com.midas.shootpointer.domain.post.entity.PostEntity;
import com.midas.shootpointer.domain.post.mapper.PostMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class PostHelperImpl implements PostHelper{
    private final PostValidation postValidation;
    private final PostUtil postUtil;
    private final PostMapper postMapper;

    @Override
    public void isValidateHighlightId(Member member, UUID highlightId) {
        postValidation.isValidateHighlightId(member,highlightId);
    }

    @Override
    public void isValidPostHashTag(Object o) {
        postValidation.isValidPostHashTag(o);
    }


    @Override
    public void isMembersPost(PostEntity postEntity, Member member) {
        postValidation.isMembersPost(postEntity,member);
    }

    @Override
    public PostEntity findPostByPostId(Long postId) {
        return postUtil.findPostByPostId(postId);
    }

    @Override
    public PostEntity save(PostEntity postEntity) {
        return postUtil.save(postEntity);
    }

    @Override
    public PostEntity update(PostEntity newPost, PostEntity oldPost, HighlightEntity highlight) {
        return postUtil.update(newPost,oldPost,highlight);
    }
  
    @Override
    public PostEntity findByPostByPostIdWithPessimisticLock(Long postId) {
        return postUtil.findByPostByPostIdWithPessimisticLock(postId);
    }

}
