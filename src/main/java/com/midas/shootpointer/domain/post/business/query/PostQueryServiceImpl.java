package com.midas.shootpointer.domain.post.business.query;

import com.midas.shootpointer.domain.post.business.PostManager;
import com.midas.shootpointer.domain.post.dto.response.PostListResponse;
import com.midas.shootpointer.domain.post.dto.response.PostResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PostQueryServiceImpl implements PostQueryService{
    private final PostManager postManager;
    @Override
    public PostResponse singleRead(Long postId) {
        return postManager.singleRead(postId);
    }

    @Override
    public PostListResponse multiRead(Long postId, int size, String type) {
        return postManager.multiRead(postId,type,size);
    }

    @Override
    public PostListResponse search(String search) {
        return postManager.getPostEntitiesByPostTitleOrPostContent(search);
    }
}
