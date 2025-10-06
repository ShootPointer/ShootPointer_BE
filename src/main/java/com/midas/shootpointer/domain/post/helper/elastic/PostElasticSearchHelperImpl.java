package com.midas.shootpointer.domain.post.helper.elastic;

import com.midas.shootpointer.domain.post.dto.response.PostSearchHit;
import com.midas.shootpointer.domain.post.dto.response.PostSort;
import com.midas.shootpointer.domain.post.entity.PostEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Profile("es")
@RequiredArgsConstructor
public class PostElasticSearchHelperImpl implements PostElasticSearchHelper{
    private final PostElasticSearchUtil postElasticSearchUtil;
    private final PostElasticSearchValidator postElasticSearchValidator;

    @Override
    public Long createPostDocument(PostEntity post) {
        return postElasticSearchUtil.createPostDocument(post);
    }

    @Override
    public List<PostSearchHit> getPostByTitleOrContentByElasticSearch(String search, int size, PostSort sort) {
        return postElasticSearchUtil.getPostByTitleOrContentByElasticSearch(search,size,sort);
    }

    @Override
    public List<String> suggestCompleteSearch(String keyword) {
        return postElasticSearchUtil.suggestCompleteSearch(keyword);
    }

    @Override
    public List<PostSearchHit> getPostByHashTagByElasticSearch(String search, int size, PostSort sort) {
        return postElasticSearchUtil.getPostByHashTagByElasticSearch(search,size,sort);
    }

    @Override
    public List<String> suggestCompleteSearchWithHashTag(String hashTag) {
        return postElasticSearchUtil.suggestCompleteSearchWithHashTag(hashTag);
    }

    @Override
    public String refinedHashTag(String hashTag) {
        return postElasticSearchUtil.refinedHashTag(hashTag);
    }

    @Override
    public boolean isHashTagSearch(String keyword) {
        return postElasticSearchValidator.isHashTagSearch(keyword);
    }
}
