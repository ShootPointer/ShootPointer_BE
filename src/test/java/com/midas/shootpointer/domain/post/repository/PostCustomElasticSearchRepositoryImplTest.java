package com.midas.shootpointer.domain.post.repository;

import com.midas.shootpointer.ElasticSearchTestContainer;
import com.midas.shootpointer.domain.post.dto.response.PostSort;
import com.midas.shootpointer.domain.post.entity.HashTag;
import com.midas.shootpointer.domain.post.entity.PostDocument;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.elasticsearch.DataElasticsearchTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataElasticsearchTest
@ActiveProfiles({"es","test"})
@Import({
        ElasticSearchTestContainer.class,
        PostCustomElasticSearchRepositoryImpl.class
})
class PostCustomElasticSearchRepositoryImplTest {
    /**
     * ============ Test scenario ================

     * 검색 정확도 (제목/내용 가중치)

     * 정렬 정확성 (score → likeCnt → postId 우선순위)

     * 자동완성 정확성 (prefix 매칭과 결과 제한 확인)

     * 엣지 케이스 (빈 검색어, 없는 값, null 등)

     */

    @Autowired
    private PostElasticSearchRepository elasticSearchRepository;

     /*
    ========================================
               [ 게시물 검색 테스트 ]
    ========================================


     */
    @Nested
    @DisplayName("게시물 검색 테스트")
    class searchTest{
        /*
        ============= 검색 정확도 =================
        */

        @Nested
        @DisplayName("검색 정확도 테스트")
        class accuracyTest{
            @AfterEach
            void cleanUp(){
                elasticSearchRepository.deleteAll();
            }

            @DisplayName("동일한 내용에 대해서 제목은 높은 가중치를 가지고 높은 _score값을 가집니다.")
            @Test
            void searchByTitleBoost(){
                //given
                PostSort sort=new PostSort(922337203685477580L,922337203685477580L,922337203685477580L);
                LocalDateTime now=LocalDateTime.now();
                Long likeCnt=123L;
                String keyword="테스트";

                String title1="엘라스틱 서치1";
                String title2="엘라스틱 서치2";
                String content1="엘라스틱 서치 테스트1";
                String content2="엘라스틱 서치 테스트2";

                String title3="엘라스틱 테스트1";
                String title4="엘라스틱 테스트2";
                String content3="엘라스틱 테스트 서치1";
                String content4="엘라스틱 테스트 서치2";

                elasticSearchRepository.saveAll(List.of(
                        //내용 - keyword
                        makePostDocument(now,title1,content1,1L,likeCnt),
                        makePostDocument(now,title2,content2,2L,likeCnt),
                        //제목 - keyword
                        makePostDocument(now,title3,content3,3L,likeCnt),
                        makePostDocument(now,title4,content4,4L,likeCnt)
                ));


                //when
                SearchHits<PostDocument> result=elasticSearchRepository.search(keyword,5,sort);

                //then
                /**
                 *  내용에 키워드가 있는 게시물 중 가장 _score가 큰 값 < 제목에 키워드가 있는 게시물 중 가장 _score가 작은 값
                 */
                double minScoreInTitle=result.stream()
                        .filter(hit->hit.getContent().getPostId()>=3L)
                        .mapToDouble(SearchHit::getScore)
                        .min()
                        .orElse(0.0);

                double maxScoreInContent=result.stream()
                        .filter(hit->hit.getContent().getPostId()<=2L)
                        .mapToDouble(SearchHit::getScore)
                        .max()
                        .orElse(0.0);

                assertThat(minScoreInTitle).isGreaterThan(maxScoreInContent);
            }

            @DisplayName("제목에 포함된 검색어가 많을수록 높은 _score값을 가집니다.")
            @Test
            void searchByTitleHaveMoreThan_score(){
                //given
                PostSort sort=new PostSort(922337203685477580L,922337203685477580L,922337203685477580L);
                LocalDateTime now=LocalDateTime.now();
                Long likeCnt=123L;
                String keyword="테스트";

                String title1="엘라스틱 테스트 테스트";
                String title2="엘라스틱 테스트 테스트 테스트";
                String title3="엘라스틱 테스트 테스트 테스트 테스트";
                String title4="엘라스틱 테스트 테스트 테스트 테스트 테스트";

                String content="내용";

                elasticSearchRepository.saveAll(List.of(
                        //내용 - keyword
                        makePostDocument(now,title1,content,1L,likeCnt),
                        makePostDocument(now,title2,content,2L,likeCnt),
                        //제목 - keyword
                        makePostDocument(now,title3,content,3L,likeCnt),
                        makePostDocument(now,title4,content,4L,likeCnt)
                ));


                //when
                SearchHits<PostDocument> result=elasticSearchRepository.search(keyword,5,sort);

                //then
                double post_1_score=0.0;
                double post_2_score=0.0;
                double post_3_score=0.0;
                double post_4_score=0.0;

                for (SearchHit<PostDocument> document:result){
                    PostDocument postDocument=document.getContent();
                    if (document.getContent().getPostId().equals(1L)) {
                        post_1_score = document.getScore();
                    }else if (document.getContent().getPostId().equals(2L)){
                        post_2_score=document.getScore();
                    }else if (document.getContent().getPostId().equals(3L)){
                        post_3_score=document.getScore();
                    }else{
                        post_4_score=document.getScore();
                    }
                }

                assertThat(post_2_score).isGreaterThan(post_1_score);
                assertThat(post_3_score).isGreaterThan(post_2_score);
                assertThat(post_4_score).isGreaterThan(post_3_score);
            }
        }
    }



    /*
    ============= 정렬 정확도 =================
     */




    /*
    ============= 자동 완성 정확도 =================
     */



    /*
    ============= 엣지 케이스 =================
     */



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
