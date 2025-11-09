package com.midas.shootpointer.domain.post.helper.simple;

import com.midas.shootpointer.domain.highlight.entity.HighlightEntity;
import com.midas.shootpointer.domain.member.entity.Member;
import com.midas.shootpointer.domain.post.business.PostOrderType;
import com.midas.shootpointer.domain.post.entity.PostEntity;
import com.midas.shootpointer.domain.post.mapper.PostMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
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
    public void isValidSize(int size) {
        postValidation.isValidSize(size);
    }

    @Override
    public boolean isValidInput(String input) {
        return postValidation.isValidInput(input);
    }

    @Override
    public PostOrderType isValidAndGetPostOrderType(String type) {
        return postUtil.isValidAndGetPostOrderType(type);
    }

    @Override
    public List<PostEntity> getPostEntitiesByPostTitleOrPostContent(String search,Long postId,int size) {
        return postUtil.getPostEntitiesByPostTitleOrPostContent(search,postId,size);
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

    @Override
    public List<PostEntity> getLatestPostListBySliceAndNoOffset(Long postId, int size) {
        return postUtil.getLatestPostListBySliceAndNoOffset(postId,size);
    }

    @Override
    public List<PostEntity> getPopularPostListBySliceAndNoOffset(Long postId, int size) {
        return postUtil.getPopularPostListBySliceAndNoOffset(postId,size);
    }
    
    @Override
    public List<Long> findPostIdsByMemberId(UUID memberId) {
        return postUtil.findPostIdsByMemberId(memberId);
    }
    
    @Override
    public List<PostEntity> findPostsByPostIds(List<Long> postIds) {
        return postUtil.findPostsByPostIds(postIds);
    }

    @Override
    public List<PostEntity> getMyLikedPost(UUID memberId, Long lastPostId, int size) {
        return postUtil.getMyLikedPost(memberId,lastPostId,size);
    }

}
