package com.midas.shootpointer.domain.post.elasticsearch.helper;

import com.midas.shootpointer.domain.post.entity.PostEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Profile("!test")  // test 프로파일이 아닐 때만 활성화
public class PostElasticSearchHelperImpl implements PostElasticSearchHelper{
    private final PostElasticSearchUtil postElasticSearchUtil;
    @Override
    public Long createPostDocument(PostEntity post) {
        return postElasticSearchUtil.createPostDocument(post);
    }
}
