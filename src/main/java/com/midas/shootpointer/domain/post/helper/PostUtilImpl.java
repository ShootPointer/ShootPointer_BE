package com.midas.shootpointer.domain.post.helper;

import com.midas.shootpointer.domain.highlight.entity.HighlightEntity;
import com.midas.shootpointer.domain.post.business.PostOrderType;
import com.midas.shootpointer.domain.post.entity.PostEntity;
import com.midas.shootpointer.domain.post.repository.PostCommandRepository;
import com.midas.shootpointer.domain.post.repository.PostQueryRepository;
import com.midas.shootpointer.global.common.ErrorCode;
import com.midas.shootpointer.global.exception.CustomException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Component
@RequiredArgsConstructor
public class PostUtilImpl implements PostUtil{
    private final PostQueryRepository postQueryRepository;
    private final PostCommandRepository postCommandRepository;

    @Override
    public PostEntity findPostByPostId(Long postId) {
        return postQueryRepository.findByPostId(postId)
                .orElseThrow(()->new CustomException(ErrorCode.IS_NOT_EXIST_POST));
    }

    @Override
    public PostEntity save(PostEntity postEntity) {
        return postCommandRepository.save(postEntity);
    }

    @Override
    public PostEntity update(PostEntity newPost, PostEntity oldPost, HighlightEntity highlight) {
        oldPost.update(
                newPost.getTitle(),
                newPost.getContent(),
                newPost.getHashTag(),
                highlight
        );
        return postCommandRepository.saveAndFlush(oldPost);
    }
  
    @Override
    @Transactional
    public PostEntity findByPostByPostIdWithPessimisticLock(Long postId) {
        return postQueryRepository.findByPostIdWithPessimisticLock(postId);
    }

    @Override
    public List<PostEntity> getLatestPostListBySliceAndNoOffset(Long postId, int size) {
        return postQueryRepository.getLatestPostListBySliceAndNoOffset(postId,size);
    }

    @Override
    public List<PostEntity> getPopularPostListBySliceAndNoOffset(Long postId, int size) {
        return postQueryRepository.getPopularPostListBySliceAndNoOffset(size,postId);
    }

    /*==========================
    *
    *PostUtilImpl
    *
    * @parm type : 조회순/인기순 정렬 type
    * @return 존재하지 않는 정렬 type일 시 CustomException 발생 / 존재하면 ENUM 클래스 반환.
    * @author kimdoyeon
    * @version 1.0.0
    * @date 25. 9. 10.
    *
    ==========================**/
    @Override
    public PostOrderType isValidPostOrderType(String type) {
        PostOrderType postOrderType;
        try {
            postOrderType=PostOrderType.valueOf(type);
        }catch (IllegalArgumentException e){
            throw new CustomException(ErrorCode.NOT_EXIST_ORDER_TYPE);
        }
        return postOrderType;
    }

}
