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

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

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
     * [ 게시물 검색 ]
     * 검색 정확도 (제목/내용 가중치)
     * 정렬 정확성 (score → likeCnt → postId 우선순위)
     * 엣지 케이스 (빈 검색어, 없는 값, null 등)

     * [ 검색아 자동 완성 ]
     * 자동완성 정확성 (prefix 매칭과 결과 제한 확인)
     * 엣지 케이스 (빈 검색어, 없는 값, null 등)
     *
     * [ 게시물 검색 - 해시태그 ]
     * 검색 정확도
     * 정렬 정확성 (score → modifiedAt → likeCnt 우선순위)
     * 엣지 케이스 (빈 검색어, 없는 값, null 등)
     *
     * [ 검색어 자동 완성 ]
     *  자동완성 정확성 (prefix 매칭과 결과 제한 확인)
     *  엣지 케이스 (빈 검색어, 없는 값, null 등)
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
        =============== 엣지 케이스 ===================
        */
        @Nested
        @DisplayName("게시물 엣지 케이스 테스트")
        class edgeCaseTest{
            @AfterEach
            void cleanUp(){
                elasticSearchRepository.deleteAll();
            }

            @DisplayName("검색어와 일치하는 게시물이 존재하지 않으면 빈 리스트를 반환합니다.")
            @Test
            void ifNotExistPostReturnToEmptyList(){
                PostSort sort=new PostSort(922337203685477580L,922337203685477580L,922337203685477580L);
                LocalDateTime now=LocalDateTime.now();
                Long likeCnt=123L;
                String keyword="알 수 없음";

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
                assertThat(result).isEmpty();
            }

             @DisplayName("빈 문자열 검색에 대해 빈 리스트를 반환합니다.")
             @Test
             void ifEmptyKeywordReturnToEmptyList(){
                 PostSort sort=new PostSort(922337203685477580L,922337203685477580L,922337203685477580L);
                 LocalDateTime now=LocalDateTime.now();
                 Long likeCnt=123L;
                 String keyword1="";
                 String keyword2=" ";

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
                 SearchHits<PostDocument> result1=elasticSearchRepository.search(keyword1,5,sort);
                 SearchHits<PostDocument> result2=elasticSearchRepository.search(keyword2,5,sort);

                 //then
                 assertThat(result1).isEmpty();
                 assertThat(result2).isEmpty();
             }

             @DisplayName("size의 개수가 0일 시 빈 리스트를 반환합니다.")
             @Test
             void ifSizeIsZeroReturnToEmptyList(){
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
                 SearchHits<PostDocument> result=elasticSearchRepository.search(keyword,0,sort);

                 //then
                 assertThat(result).isEmpty();
             }

         }
    }

    /*
    ========================================
             [ 검색어 자동완성 테스트 ]
    ========================================
     */
    @Nested
    @DisplayName("검색어 자동 완성 테스트")
    class suggestComplete{

        @Nested
        @DisplayName("검색어 자동 완성 정확성 테스트")
        class accuracyTest{
            @AfterEach
            void cleanUp(){
                elasticSearchRepository.deleteAll();
            }

            @DisplayName("검색어(제목 기준) prefix로 제목이 시작되는 경우 자동완성 결과에 포함합니다.")
            @Test
            void suggestComplete() throws IOException {
                LocalDateTime now=LocalDateTime.now();
                Long likeCnt=123L;
                String keyword="테스";

                String title1="엘라스틱 테스";//포함 X
                String title2="이거는 테스트";//포함 X
                String title3="테스 테스트 테스트 테스트";
                String title4="테스트 테스트 테스트 테스트";

                String content="내용";

                elasticSearchRepository.saveAll(List.of(
                        makePostDocument(now,title1,content,1L,likeCnt),
                        makePostDocument(now,title2,content,2L,likeCnt),
                        makePostDocument(now,title3,content,3L,likeCnt),
                        makePostDocument(now,title4,content,4L,likeCnt)
                ));

                //when
                SearchHits<PostDocument> result=elasticSearchRepository.suggestCompleteByKeyword(keyword);

                //then
                assertThat(result.getSearchHits().size()).isEqualTo(2);
                result.stream().forEach(pd->{
                    assertThat(pd.getContent().getPostId()).isGreaterThanOrEqualTo(3L);
                });
            }

            @DisplayName("검색어 prefix로 내용에 대한 키워드는 포함되지 않습니다.")
            @Test
            void suggestCompleteNotIncludeContent() throws IOException {
                LocalDateTime now=LocalDateTime.now();
                Long likeCnt=123L;
                String keyword="내용";

                String title1="엘라스틱 테스";
                String title2="이거는 테스트";
                String title3="테스 테스트 테스트 테스트";
                String title4="테스트 테스트 테스트 테스트";

                String content="내용";

                elasticSearchRepository.saveAll(List.of(
                        makePostDocument(now,title1,content,1L,likeCnt),
                        makePostDocument(now,title2,content,2L,likeCnt),
                        makePostDocument(now,title3,content,3L,likeCnt),
                        makePostDocument(now,title4,content,4L,likeCnt)
                ));

                //when
                SearchHits<PostDocument> result=elasticSearchRepository.suggestCompleteByKeyword(keyword);

                //then
                assertThat(result.getSearchHits().size()).isEqualTo(0);
            }

        }

        @Nested
        @DisplayName("검색어 자동 완성 엣지 케이스 테스트")
        class edgeTest{

            private static final String[] TITLES = {
                    "고양이", "강아지", "여행", "프로그래밍", "커피", "음악", "스포츠", "게임", "책", "영화",
                    "바다", "산", "도시", "학교", "도서관", "캠핑", "사진", "노래", "비밀", "추억",
                    "도전", "성공", "실패", "열정", "휴식", "식사", "저녁", "아침", "점심", "계획"
            };

            @AfterEach
            void cleanUp(){
                elasticSearchRepository.deleteAll();
            }

            @DisplayName("자동 완성 검색어의 개수를 최대 5개까지 반환합니다.")
            @Test
            void ifInputEmptyKeywordReturnToEmptyList() throws IOException {
                LocalDateTime now=LocalDateTime.now();
                Long likeCnt=123L;
                String keyword="테스";

                String title="테스";
                String content="내용";
                Random random=new Random();

                for (int i=0;i<10;i++){
                    elasticSearchRepository.save(
                            makePostDocument(now,title+TITLES[random.nextInt(30)],content,Long.parseLong(String.valueOf(i+1)),likeCnt)
                    );
                }

                //when
                SearchHits<PostDocument> result=elasticSearchRepository.suggestCompleteByKeyword(keyword);

                //then
                assertThat(result.getSearchHits()).hasSize(5);
            }
        }
    }

    /*
    ========================================
           [ 게시물 검색 - 해시태그 테스트 ]
    ========================================
     */

    @Nested
    @DisplayName("게시물 검색 - 해시태그 테스트")
    class searchByHashTagTest{
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

            @DisplayName("해시태그로 게시물을 조회합니다.")
            @Test
            void searchByTitleHaveMoreThan_score(){
                //given
                PostSort sort=new PostSort(922337203685477580L,922337203685477580L,922337203685477580L);
                LocalDateTime now=LocalDateTime.now();
                Long likeCnt=123L;
                String keyword1="3점슛";
                String keyword2="2점슛";

                String title="엘라스틱 테스트 테스트";

                elasticSearchRepository.saveAll(List.of(
                        makePostDocumentWithHashTag(now,now,HashTag.TREE_POINT,likeCnt,1L,title),
                        makePostDocumentWithHashTag(now,now,HashTag.TREE_POINT,likeCnt,2L,title),
                        makePostDocumentWithHashTag(now,now,HashTag.TREE_POINT,likeCnt,3L,title),
                        makePostDocumentWithHashTag(now,now,HashTag.TWO_POINT,likeCnt,4L,title),
                        makePostDocumentWithHashTag(now,now,HashTag.TWO_POINT,likeCnt,5L,title)
                ));


                //when
                SearchHits<PostDocument> expectedList1=elasticSearchRepository.searchByHashTag(keyword1,5,sort); //3점슛 해시태그
                SearchHits<PostDocument> expectedList2=elasticSearchRepository.searchByHashTag(keyword2,5,sort);//2점슛 해시태그

                //then
                assertThat(expectedList1.getSearchHits()).hasSize(3);
                assertThat(expectedList2.getSearchHits()).hasSize(2);

                expectedList1.stream().forEach(pd->{
                    assertThat(pd.getContent().getPostId()).isLessThanOrEqualTo(3L);
                });
                expectedList2.stream().forEach(pd->{
                    assertThat(pd.getContent().getPostId()).isGreaterThanOrEqualTo(4L);
                });
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

            @DisplayName("hashTag가 같으면 _score 동일, likeCnt 내림차 순으로 정렬됩니다.")
            @Test
            void searchOrderBy_postId(){
                //given
                PostSort sort=new PostSort(922337203685477580L,922337203685477580L,922337203685477580L);
                LocalDateTime now=LocalDateTime.now();
                String keyword="2점슛";

                String title="엘라스틱 테스트 테스트";

                elasticSearchRepository.saveAll(List.of(
                        makePostDocumentWithHashTag(now,now,HashTag.TWO_POINT,12L,1L,title),
                        makePostDocumentWithHashTag(now,now,HashTag.TWO_POINT,312L,2L,title),
                        makePostDocumentWithHashTag(now,now,HashTag.TWO_POINT,14124L,3L,title),
                        makePostDocumentWithHashTag(now,now,HashTag.TWO_POINT,1L,4L,title)
                ));

                //when
                SearchHits<PostDocument> result=elasticSearchRepository.searchByHashTag(keyword,5,sort);
                /*
                 postId 기준 : 3L->2L->1L->4L 순서
                 */
                List<Long> expectedResult=List.of(3L,2L,1L,4L);
                List<Long> realResult=new ArrayList<>();

                for (SearchHit<PostDocument> hit:result){
                    realResult.add(hit.getContent().getPostId());
                }

                //then
                assertThat(realResult).hasSize(4);
                for (int idx=0;idx<4;idx++){
                    assertThat(expectedResult.get(idx)).isEqualTo(realResult.get(idx));
                }
            }

            @DisplayName("hashTag가 같으면 _score 동일, likeCnt 동일, postId 내림차 순으로 정렬됩니다.")
            @Test
            void searchOrderBy_likeCnt(){
                //given
                PostSort sort=new PostSort(922337203685477580L,922337203685477580L,922337203685477580L);
                LocalDateTime now=LocalDateTime.now();
                Long likeCnt=123L;
                String keyword="2점슛";
                String title="엘라스틱 테스트 테스트";

                elasticSearchRepository.saveAll(List.of(
                      makePostDocumentWithHashTag(now,now,HashTag.TWO_POINT,likeCnt,23L,title),
                      makePostDocumentWithHashTag(now,now,HashTag.TWO_POINT,likeCnt,123123123123123L,title),
                      makePostDocumentWithHashTag(now,now,HashTag.TWO_POINT,likeCnt,13L,title)
                ));
                List<Long> expectedList=List.of(123123123123123L,23L,13L);

                //when
                SearchHits<PostDocument> result=elasticSearchRepository.searchByHashTag(keyword,5,sort);
                List<Long> postIdList=new ArrayList<>();

                for (SearchHit<PostDocument> document:result){
                    postIdList.add(document.getContent().getPostId());
                }

                //then
                //예상 순서 (postId) : 2L -> 1L -> 3L
                assertThat(postIdList).hasSize(3);
                for (int idx=0;idx<3;idx++){
                    assertThat(postIdList.get(idx)).isEqualTo(expectedList.get(idx));
                }
            }


            @DisplayName("likeCnt 값이 postId 값보다 정렬 우선순위가 높은지 확인합니다.")
            @Test
            void searchOrdersByLikeCntBeforePostId(){
                //given
                PostSort sort=new PostSort(922337203685477580L,922337203685477580L,922337203685477580L);
                LocalDateTime now=LocalDateTime.now();

                String keyword="2점슛";
                String title="엘라스틱";

                elasticSearchRepository.saveAll(List.of(
                        makePostDocumentWithHashTag(now,now,HashTag.TWO_POINT,1L,4L,title),
                        makePostDocumentWithHashTag(now,now,HashTag.TWO_POINT,2L,3L,title),
                        makePostDocumentWithHashTag(now,now,HashTag.TWO_POINT,3L,2L,title),
                        makePostDocumentWithHashTag(now,now,HashTag.TWO_POINT,4L,1L,title)
                ));
                //예상 순서 : 1L -> 2L -> 3L -> 4L
                List<Long> expectedList=List.of(1L,2L,3L,4L);

                //when
                SearchHits<PostDocument> result=elasticSearchRepository.searchByHashTag(keyword,5,sort);

                //then
                int idx=0;
                for (SearchHit<PostDocument> document:result){
                    assertThat(document.getContent().getPostId()).isEqualTo(expectedList.get(idx));
                    idx++;
                }


            }
        }

        /*
        =============== 엣지 케이스 ===================
       */
        @Nested
        @DisplayName("게시물 엣지 케이스 테스트")
        class edgeCaseTest{
            @AfterEach
            void cleanUp(){
                elasticSearchRepository.deleteAll();
            }

            @DisplayName("검색어와 일치하는 게시물이 존재하지 않으면 빈 리스트를 반환합니다.")
            @Test
            void ifNotExistPostReturnToEmptyList(){
                PostSort sort=new PostSort(922337203685477580L,922337203685477580L,922337203685477580L);
                LocalDateTime now=LocalDateTime.now();
                Long likeCnt=123L;
                String keyword="점슛";
                String title="엘라스틱 테스트 테스트";

                elasticSearchRepository.saveAll(List.of(
                        makePostDocumentWithHashTag(now,now,HashTag.TWO_POINT,likeCnt,1L,title),
                        makePostDocumentWithHashTag(now,now,HashTag.TWO_POINT,likeCnt,2L,title),
                        makePostDocumentWithHashTag(now,now,HashTag.TWO_POINT,likeCnt,3L,title),
                        makePostDocumentWithHashTag(now,now,HashTag.TREE_POINT,likeCnt,4L,title)
                ));

                //when
                SearchHits<PostDocument> result=elasticSearchRepository.searchByHashTag(keyword,5,sort);

                //then
                assertThat(result).isEmpty();
            }

            @DisplayName("빈 문자열 검색에 대해 빈 리스트를 반환합니다.")
            @Test
            void ifEmptyKeywordReturnToEmptyList(){
                PostSort sort=new PostSort(922337203685477580L,922337203685477580L,922337203685477580L);
                LocalDateTime now=LocalDateTime.now();
                Long likeCnt=123L;
                String keyword1="";
                String keyword2=" ";

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
                SearchHits<PostDocument> result1=elasticSearchRepository.searchByHashTag(keyword1,5,sort);
                SearchHits<PostDocument> result2=elasticSearchRepository.searchByHashTag(keyword2,5,sort);

                //then
                assertThat(result1).isEmpty();
                assertThat(result2).isEmpty();
            }

            @DisplayName("size의 개수가 0일 시 빈 리스트를 반환합니다.")
            @Test
            void ifSizeIsZeroReturnToEmptyList(){
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
                SearchHits<PostDocument> result=elasticSearchRepository.searchByHashTag(keyword,0,sort);

                //then
                assertThat(result).isEmpty();
            }

        }
    }

    /*
    ========================================
          [ 검색어 자동완성 - 해시태그 테스트 ]
    ========================================
     */
    @Nested
    @DisplayName("검색어 자동 완성 - 해시태그 테스트")
    class suggestCompleteByHashTag{

        @Nested
        @DisplayName("검색어 자동 완성 - 해시태그 정확성 테스트")
        class accuracyTest{
            @AfterEach
            void cleanUp(){
                elasticSearchRepository.deleteAll();
            }

            @DisplayName("검색어(해시태그 기준) prefix로 해시태그로 시작되는 경우 자동완성 결과에 포함합니다.")
            @Test
            void suggestComplete() throws IOException {
                LocalDateTime now=LocalDateTime.now();
                Long likeCnt=123L;
                String title="엘라스틱";

                String keyword1="2";
                String keyword2="2점";
                String keyword3="2점슛";

                elasticSearchRepository.saveAll(List.of(
                        makePostDocumentWithHashTag(now,now,HashTag.TREE_POINT,likeCnt,1L,title),
                        makePostDocumentWithHashTag(now,now,HashTag.TREE_POINT,likeCnt,2L,title),

                        makePostDocumentWithHashTag(now,now,HashTag.TWO_POINT,likeCnt,3L,title),
                        makePostDocumentWithHashTag(now,now,HashTag.TWO_POINT,likeCnt,4L,title),
                        makePostDocumentWithHashTag(now,now,HashTag.TWO_POINT,likeCnt,5L,title),
                        makePostDocumentWithHashTag(now,now,HashTag.TWO_POINT,likeCnt,6L,title),
                        makePostDocumentWithHashTag(now,now,HashTag.TWO_POINT,likeCnt,7L,title)
                ));

                //when
                SearchHits<PostDocument> result1=elasticSearchRepository.suggestCompleteByHashTag(keyword1);
                SearchHits<PostDocument> result2=elasticSearchRepository.suggestCompleteByHashTag(keyword2);
                SearchHits<PostDocument> result3=elasticSearchRepository.suggestCompleteByHashTag(keyword3);

                //then
                assertThat(result1.getSearchHits()).hasSize(5);
                assertThat(result2.getSearchHits()).hasSize(5);
                assertThat(result3.getSearchHits()).hasSize(5);
            }

        }

        @Nested
        @DisplayName("검색어 자동 완성 - 해시태그 엣지 케이스 테스트")
        class edgeTest{

            private static final String[] TITLES = {
                    "고양이", "강아지", "여행", "프로그래밍", "커피", "음악", "스포츠", "게임", "책", "영화",
                    "바다", "산", "도시", "학교", "도서관", "캠핑", "사진", "노래", "비밀", "추억",
                    "도전", "성공", "실패", "열정", "휴식", "식사", "저녁", "아침", "점심", "계획"
            };

            @AfterEach
            void cleanUp(){
                elasticSearchRepository.deleteAll();
            }

            @DisplayName("자동 완성 검색어-해시태그의 개수를 최대 5개까지 반환합니다.")
            @Test
            void ifInputEmptyKeywordReturnToEmptyList() throws IOException {
                LocalDateTime now=LocalDateTime.now();
                Long likeCnt=123L;
                String keyword="2점슛";
                String title="테스";

                for (int i=0;i<10;i++){
                    elasticSearchRepository.save(
                            makePostDocumentWithHashTag(now,now,HashTag.TWO_POINT,likeCnt, (long) (i + 1),title)
                    );
                }

                //when
                SearchHits<PostDocument> result=elasticSearchRepository.suggestCompleteByHashTag(keyword);

                //then
                assertThat(result.getSearchHits()).hasSize(5);
            }
        }
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

    private PostDocument makePostDocumentWithHashTag(LocalDateTime modifiedTime,
                                                     LocalDateTime createdTime,
                                                     HashTag tag,
                                                     Long likeCnt,
                                                     Long postId,
                                                     String title
    ){
        return PostDocument.builder()
                .title(title)
                .content("content")
                .postId(postId)
                .modifiedAt(modifiedTime)
                .createdAt(createdTime)
                .likeCnt(likeCnt)
                .memberName("test")
                .hashTag(tag.getName())
                .highlightUrl("url")
                .build();
    }
}
