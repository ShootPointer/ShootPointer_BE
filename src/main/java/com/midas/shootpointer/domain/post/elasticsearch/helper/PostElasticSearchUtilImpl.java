package com.midas.shootpointer.domain.post.elasticsearch.helper;

import com.midas.shootpointer.domain.post.dto.response.PostResponse;
import com.midas.shootpointer.domain.post.elasticsearch.PostDocument;
import com.midas.shootpointer.domain.post.elasticsearch.PostElasticSearchRepository;
import com.midas.shootpointer.domain.post.elasticsearch.mapper.PostElasticSearchMapper;
import com.midas.shootpointer.domain.post.entity.PostEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.data.domain.Sort;
import org.springframework.data.elasticsearch.client.elc.NativeQuery;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.elasticsearch.core.query.Query;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Component
@Profile("!test")  // test 프로파일이 아닐 때만 활성화
public class PostElasticSearchUtilImpl implements PostElasticSearchUtil{
    private final PostElasticSearchRepository postElasticSearchRepository;
    private final ElasticsearchOperations elasticsearchOperations;
    private final PostElasticSearchMapper mapper;
    @Transactional
    @Override
    public Long createPostDocument(PostEntity post) {
        PostDocument postDocument=mapper.entityToDoc(post);
        return postElasticSearchRepository.save(postDocument).getPostId();
    }

    /*==========================
    *
    *PostElasticSearchUtilImpl
    *
    * @parm search : 검색명
    * @return 제목 또는 내용으로 게시글 검색.
    * @author kimdoyeon
    * @version 1.0.0
    * @date 25. 9. 22.
    *
    ==========================**/
    @Override
    public List<PostResponse> getPostByTitleOrContentByElasticSearch(String search, Long lastPostId, int size) {
        /**
         * 1. 쿼리 생성.
         * 1) 제목 + 내용 게시물 조회 - NoOffset+slice 방식
         * 2) 조회된 게시물 최신순 정렬 반환.
         */
        Query query=buildSearch(search,lastPostId,size);

        SearchHits<PostDocument> hits=elasticsearchOperations.search(query,PostDocument.class);

        /**
         * 2. PostDocument -> PostResponse 변환.
         */
        return hits.getSearchHits().stream()
                .map(hit->mapper.docToResponse(hit.getContent()))
                .collect(Collectors.toList());

    }

    private NativeQuery buildSearch(String search,Long lastPostId,int size){
        return NativeQuery.builder()
                .withQuery(q->q
                        .bool(b->b
                                .should(s -> s.match(m -> m.field("title").query(search)))
                                .should(s -> s.match(m -> m.field("content").query(search)))
                                .minimumShouldMatch("1")
                        )
                )
                .withSort(Sort.by(Sort.Order.desc("postId")))
                .withSearchAfter(Collections.singletonList(lastPostId))
                .withMaxResults(size)
                .build();
    }


}
