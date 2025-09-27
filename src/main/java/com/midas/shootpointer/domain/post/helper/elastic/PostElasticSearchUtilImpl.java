package com.midas.shootpointer.domain.post.helper.elastic;

import co.elastic.clients.elasticsearch.core.SearchResponse;
import com.midas.shootpointer.domain.post.dto.response.PostSearchHit;
import com.midas.shootpointer.domain.post.dto.response.PostSort;
import com.midas.shootpointer.domain.post.entity.PostDocument;
import com.midas.shootpointer.domain.post.entity.PostEntity;
import com.midas.shootpointer.domain.post.mapper.PostElasticSearchMapper;
import com.midas.shootpointer.domain.post.repository.PostElasticSearchRepository;
import com.midas.shootpointer.global.annotation.CustomLog;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.ArrayList;
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
    public List<PostSearchHit> getPostByTitleOrContentByElasticSearch(String search, int size, PostSort sort) {
        SearchHits<PostDocument> documentList=postElasticSearchRepository.search(search,size,sort);

        /**
         * 조회된 Document가 없는 경우 빈 리스트 반환
         */
        if(documentList==null) return Collections.emptyList();

        List<PostSearchHit> responses=new ArrayList<>();
        for (SearchHit<PostDocument> hit:documentList){
            PostDocument doc=hit.getContent();
            float _score=hit.getScore();
            //PostResponse 값 , _score 값 저장
            responses.add(new PostSearchHit(doc,_score));
        }

        return responses;
    }

    @Override
    @CustomLog
    public List<String> suggestCompleteSearch(String keyword) {
        try {
            SearchResponse<PostDocument> searchResponse=
                    postElasticSearchRepository.suggestCompleteByKeyword(keyword);

            return searchResponse.hits().hits().stream()
                    .map(hit->hit.source().getTitle())
                    .toList();

        }catch (IOException e){
            return Collections.emptyList();
        }
    }

}
