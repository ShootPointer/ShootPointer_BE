package com.midas.shootpointer.domain.post.elasticsearch.helper;

import com.midas.shootpointer.domain.post.elasticsearch.PostDocument;
import com.midas.shootpointer.domain.post.elasticsearch.PostElasticSearchRepository;
import com.midas.shootpointer.domain.post.entity.PostEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Component
@Profile("!test")  // test 프로파일이 아닐 때만 활성화
public class PostElasticSearchUtilImpl implements PostElasticSearchUtil{
    private final PostElasticSearchRepository postElasticSearchRepository;

    @Transactional
    @Override
    public Long createPostDocument(PostEntity post) {
        PostDocument postDocument=PostDocument.of(post);
        return postElasticSearchRepository.save(postDocument).getId();
    }


}
