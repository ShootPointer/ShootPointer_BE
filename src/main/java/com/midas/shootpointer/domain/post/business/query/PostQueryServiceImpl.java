package com.midas.shootpointer.domain.post.business.query;

import com.midas.shootpointer.domain.post.business.PostManager;
import com.midas.shootpointer.domain.post.dto.response.PostListResponse;
import com.midas.shootpointer.domain.post.dto.response.PostResponse;
import com.midas.shootpointer.domain.post.dto.response.PostSort;
import com.midas.shootpointer.domain.post.dto.response.SearchAutoCompleteResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

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
    public PostListResponse search(String search,Long postId,int size) {
        return postManager.getPostEntitiesByPostTitleOrPostContent(search,postId,size);
    }

    @Override
    public PostListResponse searchByElastic(String search, int size,PostSort sort) {
        return postManager.getPostByPostTitleOrPostContentByElasticSearch(search,size,sort);
    }

    @Override
    public List<SearchAutoCompleteResponse> suggest(String keyword) {
        return postManager.suggest(keyword);
    }
    
    @Override
    public PostListResponse getMyPosts(UUID memberId) {
        return postManager.getMyPosts(memberId);
    }

    @Override
    public PostListResponse myLikedPost(UUID memberId, Long lastPostId, int size) {
        return postManager.getMyLikedPosts(memberId,lastPostId,size);
    }
}
