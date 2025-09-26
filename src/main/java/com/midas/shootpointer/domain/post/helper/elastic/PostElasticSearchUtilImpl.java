package com.midas.shootpointer.domain.post.helper.elastic;

import com.midas.shootpointer.domain.post.dto.response.PostResponse;
import com.midas.shootpointer.domain.post.entity.PostDocument;
import com.midas.shootpointer.domain.post.entity.PostEntity;
import com.midas.shootpointer.domain.post.mapper.PostElasticSearchMapper;
import com.midas.shootpointer.domain.post.repository.PostElasticSearchRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;

@RequiredArgsConstructor
@Component
@Profile("!dev")  // dev 프로파일이 아닐 때만 활성화
public class PostElasticSearchUtilImpl implements PostElasticSearchUtil{
    private final PostElasticSearchRepository postElasticSearchRepository;
    private final PostElasticSearchMapper mapper;

    @Transactional
    @Override
    public Long createPostDocument(PostEntity post) {
        PostDocument postDocument=mapper.entityToDoc(post);
        return postElasticSearchRepository.save(postDocument).getPostId();
    }

    @Transactional(readOnly = true)
    @Override
    public List<PostResponse> getPostByTitleOrContentByElasticSearch(String search,
                                                                     int size,
                                                                     double _score,
                                                                     Long likeCnt,
                                                                     Long lastPostId) {
        List<PostDocument> documentList=postElasticSearchRepository.search(search,size,_score,likeCnt,lastPostId)
                .orElse(Collections.emptyList());

        /**
         * Document 형식으로 매핑
         */
        return documentList.stream()
                .map(mapper::docToResponse)
                .toList();
    }

}
