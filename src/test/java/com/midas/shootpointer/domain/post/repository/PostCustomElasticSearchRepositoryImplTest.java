package com.midas.shootpointer.domain.post.repository;

import com.midas.shootpointer.ElasticSearchTestContainer;
import com.midas.shootpointer.domain.post.entity.HashTag;
import com.midas.shootpointer.domain.post.entity.PostDocument;
import com.midas.shootpointer.global.annotation.CustomLog;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.elasticsearch.DataElasticsearchTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.test.context.ActiveProfiles;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@DataElasticsearchTest
@ActiveProfiles({"es","test"})
@Import({
        ElasticSearchTestContainer.class,
        PostCustomElasticSearchRepositoryImpl.class
})
class PostCustomElasticSearchRepositoryImplTest {

    @Autowired
    private PostElasticSearchRepository elasticSearchRepository;

    @Test
    void search() {

    }

    @Test
    @DisplayName("게시물 검색 시 검색어를 5개 까지 추천합니다.(검색어 : 테스)")
    @CustomLog
    void suggestCompleteByKeyword() throws IOException {
        //given
        String keyword="테스"; //테스트할 검색어.

        String title_1="테스트 환경";
        String title_2="테스트 2환경";
        String title_3="테스 환경";
        String title_4="테스환경";
        String title_5="테스경";

        LocalDateTime now=LocalDateTime.now();
        String content="content";
        Long likeCnt=123L;

        elasticSearchRepository.saveAll(List.of(
                makePostDocument(now,title_1,content,1L,likeCnt),
                makePostDocument(now,title_2,content,2L,likeCnt),
                makePostDocument(now,title_3,content,3L,likeCnt),
                makePostDocument(now,title_4,content,4L,likeCnt),
                makePostDocument(now,title_5,content,5L,likeCnt)
        ));

        //when
        SearchHits<PostDocument> results=elasticSearchRepository.suggestCompleteByKeyword(keyword);

        //then
        assertThat(results.getSearchHits().size()).isEqualTo(5);

    }

    /**
     * Mock PostDocument
     */
    private PostDocument makePostDocument(LocalDateTime time,
                                          String title,
                                          String content,
                                          Long postId,
                                          Long likeCnt
    ) {
        return PostDocument.builder()
                .title(title)
                .content(content)
                .postId(postId)
                .modifiedAt(time)
                .createdAt(time)
                .likeCnt(likeCnt)
                .memberName("test")
                .hashTag(HashTag.TREE_POINT.getName())
                .highlightUrl("url")
                .build();
    }
}
