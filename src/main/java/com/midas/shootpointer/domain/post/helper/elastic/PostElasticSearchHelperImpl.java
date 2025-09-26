package com.midas.shootpointer.domain.post.helper.elastic;

import com.midas.shootpointer.domain.post.dto.response.PostResponse;
import com.midas.shootpointer.domain.post.dto.response.PostSort;
import com.midas.shootpointer.domain.post.entity.PostEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Profile("!dev")  // dev 프로파일이 아닐 때만 활성화
@RequiredArgsConstructor
public class PostElasticSearchHelperImpl implements PostElasticSearchHelper{
    private final PostElasticSearchUtil postElasticSearchUtil;

    @Override
    public Long createPostDocument(PostEntity post) {
        return postElasticSearchUtil.createPostDocument(post);
    }

    @Override
    public List<PostResponse> getPostByTitleOrContentByElasticSearch(String search,int size, PostSort sort) {
        return postElasticSearchUtil.getPostByTitleOrContentByElasticSearch(search,size,sort);
    }

}
