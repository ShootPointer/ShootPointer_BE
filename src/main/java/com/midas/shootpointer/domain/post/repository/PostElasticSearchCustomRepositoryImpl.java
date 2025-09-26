package com.midas.shootpointer.domain.post.repository;

import co.elastic.clients.elasticsearch._types.SortOrder;
import co.elastic.clients.elasticsearch.core.SearchRequest;
import com.midas.shootpointer.domain.post.entity.PostDocument;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class PostElasticSearchCustomRepositoryImpl implements PostElasticsearchCustomRepository {
    //제목 가중치
    private static final float TITLE_WEIGHT = 30f;

    //내용 가중치
    private static final float CONTENT_WEIGHT = 10f;

    /**
     * 게시물 검색 조회
     *
     * @param search : 검색명
     * @param size   : 불러올 크기
     * @return PostDocument
     */
    @Override
    public List<PostDocument> search(String search, int size) {
        /**
         * 조건
         * 1. 제목 search 포함 : 가중치 +30
         * 2. 내용 search 포함 : 가중치 +10
         * 3. Elastic Search score 이용
         * 4. 점수 동일 시, like_cnt 내림차순
         */
        SearchRequest searchRequest = new SearchRequest.Builder()
                .index("post")
                .query(q -> q
                        .functionScore(fs -> fs
                                .query(qq -> qq
                                        .bool(b -> b
                                                .should(s -> s.match(m -> m
                                                        .field("title")
                                                        .query(search)
                                                        .boost(TITLE_WEIGHT)))
                                                .should(s -> s.match(m -> m
                                                        .field("content")
                                                        .query(search)
                                                        .boost(CONTENT_WEIGHT)))
                                        )
                                )
                        )
                        //ES _socre 내림차순 정렬
                ).sort(s -> s.score(sc -> sc.order(SortOrder.Desc)))
                //좋아요  내림차순
                .sort(s -> s.field(f -> f.field("likeCnt").order(SortOrder.Desc)))
                .size(size)
                .build();

        return List.of();
    }
}
