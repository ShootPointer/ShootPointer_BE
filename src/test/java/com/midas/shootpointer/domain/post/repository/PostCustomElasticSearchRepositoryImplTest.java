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
import java.util.ArrayList;
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
                    if (postDocument.getPostId().equals(1L)) {
                        post_1_score = document.getScore();
                    }else if (postDocument.getPostId().equals(2L)){
                        post_2_score=document.getScore();
                    }else if (postDocument.getPostId().equals(3L)){
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

        /*
        ============= 정렬 정확도 =================
        */
         @Nested
         @DisplayName("정렬 정확도 테스트")
         class sortAccuracyTest{
            @AfterEach
            void cleanUp(){
                elasticSearchRepository.deleteAll();
            }

            @DisplayName("_score 값에 따라 내림차순으로 정렬됩니다.")
            @Test
            void searchOrderBy_score(){
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
                        makePostDocument(now,title1,content,1L,likeCnt),
                        makePostDocument(now,title2,content,2L,likeCnt),
                        makePostDocument(now,title3,content,3L,likeCnt),
                        makePostDocument(now,title4,content,4L,likeCnt)
                ));

                //when
                SearchHits<PostDocument> result=elasticSearchRepository.search(keyword,5,sort);

                //then
                /*
                 _score DESC : 4L->3L->2L->1L 순서
                 */
                Long initPostId=4L;
                for (SearchHit<PostDocument> hit:result){
                    assertThat(hit.getContent().getPostId()).isEqualTo(initPostId);
                    initPostId--;
                }

            }

            @DisplayName("좋아요 개수(likeCnt) 값에 따라 내림차순 정렬됩니다.")
            @Test
            void searchOrderBy_likeCnt(){
                //given
                PostSort sort=new PostSort(922337203685477580L,922337203685477580L,922337203685477580L);
                LocalDateTime now=LocalDateTime.now();
                Long likeCnt=123L;
                String keyword="테스트";

                String title="엘라스틱 테스트 테스트";

                String content="내용";

                elasticSearchRepository.saveAll(List.of(
                        makePostDocument(now,title,content,1L,likeCnt+232L),
                        makePostDocument(now,title,content,2L,likeCnt+112L),
                        makePostDocument(now,title,content,3L,likeCnt+12L),
                        makePostDocument(now,title,content,4L,likeCnt+3333L)
                ));
                List<Long> expectedList=List.of(4L,1L,2L,3L);

                //when
                SearchHits<PostDocument> result=elasticSearchRepository.search(keyword,5,sort);
                List<Long> postIdList=new ArrayList<>();

                for (SearchHit<PostDocument> document:result){
                    postIdList.add(document.getContent().getPostId());
                }

                //then
                //예상 순서 (postId) : 4L -> 1L -> 2L -> 3L
                for (int idx=0;idx<4;idx++){
                    assertThat(postIdList.get(idx)).isEqualTo(expectedList.get(idx));
                }
            }

            @DisplayName("postId 값에 따라 내림차순 정렬됩니다.")
            @Test
            void searchOrderByPostId(){
                //given
                PostSort sort=new PostSort(922337203685477580L,922337203685477580L,922337203685477580L);
                LocalDateTime now=LocalDateTime.now();
                Long likeCnt=123L;
                String keyword="테스트";

                String title="엘라스틱 테스트 테스트";

                String content="내용";

                elasticSearchRepository.saveAll(List.of(
                        makePostDocument(now,title,content,11L,likeCnt),
                        makePostDocument(now,title,content,2234L,likeCnt),
                        makePostDocument(now,title,content,123L,likeCnt),
                        makePostDocument(now,title,content,43L,likeCnt)
                ));
                //예상 순서 : 2234L->123L->43L->11L
                List<Long> expectedList=List.of(2234L,123L,43L,11L);

                //when
                SearchHits<PostDocument> result=elasticSearchRepository.search(keyword,5,sort);

                //then
                int idx=0;
                for (SearchHit<PostDocument> document:result){
                    assertThat(document.getContent().getPostId()).isEqualTo(expectedList.get(idx));
                    idx++;
                }
            }

            @DisplayName("_score 값이 좋아요 개수(likeCnt) 값보다 정렬 우선순위가 높은지 확인합니다.")
            @Test
            void searchOrdersByScoreBeforeLikeCount(){
                //given
                PostSort sort=new PostSort(922337203685477580L,922337203685477580L,922337203685477580L);
                LocalDateTime now=LocalDateTime.now();

                Long likeCnt1=123L;
                Long likeCnt2=3824792831123123423L;
                Long likeCnt3=123219310923123L;

                String keyword="테스트";

                String title1="엘라스틱 테스트 테스트 테스트 테스트 테스트 테스트 테스트 테스트 테스트 테스트";
                String title2="엘라스틱 테스트 테스트 테스트 테스트";
                String title3="엘라스틱 테스트";

                String content="내용";

                elasticSearchRepository.saveAll(List.of(
                        makePostDocument(now,title1,content,1L,likeCnt1),
                        makePostDocument(now,title2,content,2L,likeCnt2),
                        makePostDocument(now,title3,content,3L,likeCnt3)
                ));
                //예상 순서 : 1L -> 2L -> 3L
                List<Long> expectedList=List.of(1L,2L,3L);

                //when
                SearchHits<PostDocument> result=elasticSearchRepository.search(keyword,5,sort);

                //then
                int idx=0;
                for (SearchHit<PostDocument> document:result){
                    assertThat(document.getContent().getPostId()).isEqualTo(expectedList.get(idx));
                    idx++;
                }

            }

            @DisplayName("_score 값이 postId 값보다 정렬 우선순위가 높은지 확인합니다.")
            @Test
            void searchOrdersByScoreBeforePostId(){
                //given
                PostSort sort=new PostSort(922337203685477580L,922337203685477580L,922337203685477580L);
                LocalDateTime now=LocalDateTime.now();

                Long likeCnt=123L;

                String keyword="테스트";

                String title1="엘라스틱 테스트 테스트 테스트 테스트 테스트 테스트 테스트 테스트 테스트 테스트";
                String title2="엘라스틱 테스트 테스트 테스트 테스트";
                String title3="엘라스틱 테스트";

                String content="내용";

                elasticSearchRepository.saveAll(List.of(
                        makePostDocument(now,title1,content,1L,likeCnt),
                        makePostDocument(now,title2,content,212124L,likeCnt),
                        makePostDocument(now,title3,content,999999999999L,likeCnt)
                ));
                //예상 순서 : 1L -> 2L -> 3L
                List<Long> expectedList=List.of(1L,212124L,999999999999L);

                //when
                SearchHits<PostDocument> result=elasticSearchRepository.search(keyword,5,sort);

                //then
                int idx=0;
                for (SearchHit<PostDocument> document:result){
                    assertThat(document.getContent().getPostId()).isEqualTo(expectedList.get(idx));
                    idx++;
                }


            }

            @DisplayName("likeCnt 값이 postId 값보다 정렬 우선순위가 높은지 확인합니다.")
            @Test
            void searchOrdersByLikeCntBeforePostId(){
                //given
                PostSort sort=new PostSort(922337203685477580L,922337203685477580L,922337203685477580L);
                LocalDateTime now=LocalDateTime.now();

                Long likeCnt1=123L;
                Long likeCnt2=3824792831123123423L;
                Long likeCnt3=123219310923123L;

                String keyword="테스트";

                String title="엘라스틱 테스트 테스트 테스트 테스트 테스트 테스트 테스트 테스트 테스트 테스트";
                String content="내용";

                elasticSearchRepository.saveAll(List.of(
                        makePostDocument(now,title,content,1L,likeCnt1),
                        makePostDocument(now,title,content,2L,likeCnt2),
                        makePostDocument(now,title,content,3L,likeCnt3)
                ));
                //예상 순서 : 2L -> 3L -> 1L
                List<Long> expectedList=List.of(2L,3L,1L);

                //when
                SearchHits<PostDocument> result=elasticSearchRepository.search(keyword,5,sort);

                //then
                int idx=0;
                for (SearchHit<PostDocument> document:result){
                    assertThat(document.getContent().getPostId()).isEqualTo(expectedList.get(idx));
                    idx++;
                }

            }

        }


        /*
        ============= 자동 완성 정확도 =================
        */
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
