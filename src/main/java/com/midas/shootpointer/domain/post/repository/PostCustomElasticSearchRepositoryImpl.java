package com.midas.shootpointer.domain.post.repository;

import co.elastic.clients.elasticsearch._types.SortOptions;
import co.elastic.clients.elasticsearch._types.SortOrder;
import com.midas.shootpointer.domain.post.dto.response.PostSort;
import com.midas.shootpointer.domain.post.entity.PostDocument;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.data.elasticsearch.client.elc.NativeQuery;
import org.springframework.data.elasticsearch.client.elc.NativeQueryBuilder;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.elasticsearch.core.query.Criteria;
import org.springframework.data.elasticsearch.core.query.CriteriaQuery;

import java.io.IOException;
import java.util.List;


@RequiredArgsConstructor
@Profile("es")
public class PostCustomElasticSearchRepositoryImpl implements PostCustomElasticSearchRepository {
    private final ElasticsearchOperations operations;

    //제목 가중치
    private final float TITLE_WEIGHT = 40f;
    //내용 가중치
    private final float CONTENT_WEIGHT = 10f;

    @Override
    public SearchHits<PostDocument> search(String search, int size, PostSort sort) {
        /**
         * 조건
         * 1. 제목 search 포함 : 가중치 +30
         * 2. 내용 search 포함 : 가중치 +10
         * 3. Elastic Search score 내림차순
         * 4. 점수 동일 시, like_cnt 내림차순
         * 5. score, like_cnt 동일 시 post_id 내림차순
         */


        //Criteria 생성하여 title 필드, content 필드에서 부분 일치 조건 + 가중치 지정
        Criteria criteria=Criteria.where("title").matches(search).boost(TITLE_WEIGHT)
                .or(Criteria.where("content").matches(search).boost(CONTENT_WEIGHT));
        CriteriaQuery booleanQuery=new CriteriaQuery(criteria);

        NativeQueryBuilder builder = NativeQuery.builder()
                .withQuery(booleanQuery)
                // sort: _score desc, likeCnt desc, postId desc
                .withSort(SortOptions.of(s -> s.score(sc -> sc.order(SortOrder.Desc))))
                .withSort(SortOptions.of(s -> s.field(f -> f.field("likeCnt").order(SortOrder.Desc))))
                .withSort(SortOptions.of(s -> s.field(f -> f.field("postId").order(SortOrder.Desc))))
                .withMaxResults(size);

        // search_after
        if (sort != null) {
            builder.withSearchAfter(sortToList(sort));
        }

        NativeQuery query = builder.build();
        return operations.search(query, PostDocument.class);
    }

    @Override
    public SearchHits<PostDocument> suggestCompleteByKeyword(String keyword) throws IOException {
        NativeQuery nativeQuery=NativeQuery.builder()
                .withQuery(q->q.matchPhrasePrefix(p->p
                        .field("title")
                        .query(keyword)
                ))
                .withMaxResults(5)
                .build();

        return operations.search(nativeQuery,PostDocument.class);
    }

    /**
     * search_after용 리스트 반환
     */
    private List<Object> sortToList(PostSort sort) {
        return List.of(sort._score(), sort.likeCnt(), sort.lastPostId());
    }
}
