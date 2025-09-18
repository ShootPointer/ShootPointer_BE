package com.midas.shootpointer.domain.post.repository;

import com.midas.shootpointer.domain.highlight.entity.HighlightEntity;
import com.midas.shootpointer.domain.highlight.repository.HighlightCommandRepository;
import com.midas.shootpointer.domain.member.entity.Member;
import com.midas.shootpointer.domain.member.repository.MemberCommandRepository;
import com.midas.shootpointer.domain.post.entity.HashTag;
import com.midas.shootpointer.domain.post.entity.PostEntity;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.time.temporal.ChronoUnit;
import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("dev")
class PostQueryRepositoryTest {
    @Autowired
    private PostQueryRepository postQueryRepository;

    @Autowired
    private PostCommandRepository postCommandRepository;

    @Autowired
    private MemberCommandRepository memberRepository;

    @Autowired
    private HighlightCommandRepository highlightCommandRepository;

    @BeforeEach
    void setUpClean() {
        postCommandRepository.deleteAll();
        highlightCommandRepository.deleteAll();
        memberRepository.deleteAll();
    }

    @AfterEach
    void tearDown() {
        postCommandRepository.deleteAll();
        highlightCommandRepository.deleteAll();
        memberRepository.deleteAll();
    }

    private Member member;
    private HighlightEntity highlight;

    private static final int LIMIT=100;

    /*
    ======================================일반 데이터 사용 쿼리 테스트======================================
     */

    @DisplayName("게시물 최신순으로 조회 시 조건에 맞는 게시물이 존재하지 않으면 빈 배열을 반환합니다.")
    @Test
    void getLatestPostListBySliceAndNoOffset_EMPTY_ARRAY(){
        //given
        Long postId=123124L;
        int size=10;

        // when
        List<PostEntity> postEntities=postQueryRepository.getLatestPostListBySliceAndNoOffset(postId,size);

        //then
        assertThat(postEntities).isEmpty();
    }

    @DisplayName("게시물 좋아요순으로 조회 시 조건에 맞는 게시물이 존재하지 않으면 빈 배열을 반환합니다.")
    @Test
    void getPopularPostListBySliceAndNoOffset_EMPTY_ARRAY(){
        //given
        Long likeCnt=123124L;
        int size=10;

        // when
        List<PostEntity> postEntities=postQueryRepository.getPopularPostListBySliceAndNoOffset(size,likeCnt);

        //then
        assertThat(postEntities).isEmpty();
    }

    @DisplayName("게시물의 제목과 내용으로 게시물을 조회하고 최신순으로 정렬하여 반환합니다. - content로 조회.")
    @Test
    void getPostEntitiesByPostTitleOrPostContentOrderByCreatedAtDesc_CONTENT() throws InterruptedException {
        //given
        member=memberRepository.save(makeMember());
        highlight=highlightCommandRepository.save(makeHighlight(member));

        List<PostEntity> expectedPostEntities=new ArrayList<>();
        //3개 생성.
        for (int i=0;i<3;i++){
            //0.1초씩 기다림 - 생성 날짜 구별을 위해
            Thread.sleep(100);
            PostEntity post=postCommandRepository.save(
                    PostEntity.builder()
                            .member(member)
                            .highlight(highlight)
                            .content("내용1 + 내용 2 + 내용 "+i)
                            .title("제목 1 + 제목2 + 제목 "+i)
                            .hashTag(HashTag.TREE_POINT)
                            .likeCnt(10L)
                            .build()
            );
            expectedPostEntities.add(post);
        }
        //최신 순의 나열
        expectedPostEntities.sort((a,b)->b.getCreatedAt().compareTo(a.getCreatedAt()));

        //when
        List<PostEntity> getPostEntity=postQueryRepository.getPostEntitiesByPostTitleOrPostContentOrderByCreatedAtDesc("내용");

        //then
        assertThat(getPostEntity).isNotEmpty();
        assertThat(getPostEntity.size()).isEqualTo(3);
        for (int i=0;i<3;i++){
            assertThat(getPostEntity.get(i).getPostId()).isEqualTo(expectedPostEntities.get(i).getPostId());
            assertThat(getPostEntity.get(i).getTitle()).isEqualTo(expectedPostEntities.get(i).getTitle());
            assertThat(getPostEntity.get(i).getContent()).isEqualTo(expectedPostEntities.get(i).getContent());
        }

    }

    @DisplayName("게시물의 제목과 내용으로 게시물을 조회하고 최신순으로 정렬하여 반환합니다. - title로 조회.")
    @Test
    void getPostEntitiesByPostTitleOrPostContentOrderByCreatedAtDesc_TITLE() throws InterruptedException {
        //given
        member=memberRepository.save(makeMember());
        highlight=highlightCommandRepository.save(makeHighlight(member));

        List<PostEntity> expectedPostEntities=new ArrayList<>();
        //3개 생성.
        for (int i=0;i<3;i++){
            //0.1초씩 기다림 - 생성 날짜 구별을 위해
            Thread.sleep(100);
            PostEntity post=postCommandRepository.save(
                    PostEntity.builder()
                            .member(member)
                            .highlight(highlight)
                            .content("내용1 + 내용 2 + 내용 "+i)
                            .title("제목 1 + 제목2 + 제목 "+i)
                            .hashTag(HashTag.TREE_POINT)
                            .likeCnt(10L)
                            .build()
            );
            expectedPostEntities.add(post);
        }
        //최신 순의 나열
        expectedPostEntities.sort((a,b)->b.getCreatedAt().compareTo(a.getCreatedAt()));

        //when
        List<PostEntity> getPostEntity=postQueryRepository.getPostEntitiesByPostTitleOrPostContentOrderByCreatedAtDesc("제목");

        //then
        assertThat(getPostEntity).isNotEmpty();
        assertThat(getPostEntity.size()).isEqualTo(3);
        for (int i=0;i<3;i++){
            assertThat(getPostEntity.get(i).getPostId()).isEqualTo(expectedPostEntities.get(i).getPostId());
            assertThat(getPostEntity.get(i).getTitle()).isEqualTo(expectedPostEntities.get(i).getTitle());
            assertThat(getPostEntity.get(i).getContent()).isEqualTo(expectedPostEntities.get(i).getContent());
        }

    }

    /*
    ======================================대용량 데이터 사용 쿼리 테스트======================================
     */

    @Nested
    class PostQueryRepositoryTestForBulk{
        private PriorityQueue<PostEntity> postEntitiesOrderByCreatedAtDesc=new PriorityQueue<>((a,b)->b.getCreatedAt().compareTo(a.getCreatedAt()));
        /**
         * 정렬 순서
         * 1순위 : 좋아요 많은 순서
         * 2순위 : 최신순
         */
        private PriorityQueue<PostEntity> postEntitiesOrderByLikeCntDesc=new PriorityQueue<>((a,b)->a.getLikeCnt().equals(b.getLikeCnt()) ?
                b.getCreatedAt().compareTo(a.getCreatedAt()): b.getLikeCnt().compareTo(a.getLikeCnt()));

        @BeforeEach
        void setUp() throws InterruptedException {
            set(postEntitiesOrderByCreatedAtDesc,postEntitiesOrderByLikeCntDesc);
        }

        @AfterEach
        void delete(){
            clean();
            postEntitiesOrderByCreatedAtDesc.clear();
            postEntitiesOrderByLikeCntDesc.clear();
        }

        @DisplayName("1_000개의 게시물을 최신순으로 불러옵니다.")
        @Test
        void getLatestPostListBySliceAndNoOffset(){
            /*
            * given
            * 1. 100개의 게시물이 @BeforeEach에 의해 1초 단위로 저장됩니다.
            * 2. 100개의 게시물이 PriorityQueue 형태로 저장됩니다.
            * 3. 우선순위 큐로 set() 메소드에서 미리 저장한 게시물 엔티티들을 최신순으로 정렬합니다.
            */


            /*
            * when : getLatestPostListBySliceAndNoOffset()를 호출하여 100개의 게시물을 최신순으로 조회합니다.
            */
            Long lastPostId=922337203685477580L;
            int idx=0;
            List<PostEntity> findPostEntities=postQueryRepository.getLatestPostListBySliceAndNoOffset(lastPostId,LIMIT);

            /*
            * then : 우선순위 큐와 실제로 조회된 게시물의 postId가 일치하는지 확인합니다.
            */
            //개수 일치 확인
            assertThat(postEntitiesOrderByCreatedAtDesc.size()).isEqualTo(LIMIT);

            while (!postEntitiesOrderByCreatedAtDesc.isEmpty()){
                PostEntity nowPost=postEntitiesOrderByCreatedAtDesc.poll();
                assertThat(nowPost.getPostId()).isEqualTo(findPostEntities.get(idx).getPostId());
                //밀리초까지만 비교
                assertThat(nowPost.getCreatedAt().truncatedTo(ChronoUnit.SECONDS)).isEqualTo(findPostEntities.get(idx).getCreatedAt().truncatedTo(ChronoUnit.SECONDS));
                idx++;
            }
        }

        @DisplayName("1_000개의 게시물을 좋아요순으로 불러옵니다.")
        @Test
        void getPopularPostListBySliceAndNoOffset(){
            /*
             * given
             * 1. 100개의 게시물이 @BeforeEach에 의해 1초 단위로 저장됩니다.
             * 2. 100개의 게시물이 PriorityQueue 형태로 저장됩니다.
             * 3. 우선순위 큐로 set() 메소드에서 미리 저장한 게시물 엔티티들을 좋아요순으로 정렬합니다.
             */


            /*
             * when : getLatestPostListBySliceAndNoOffset()를 호출하여 100개의 게시물을 좋아요순으로 조회합니다.
             */
            Long lastPostId=922337203685477580L;

            int idx=0;
            List<PostEntity> findPostEntities=postQueryRepository.getPopularPostListBySliceAndNoOffset(LIMIT,lastPostId);

            /*
             * then : 우선순위 큐와 실제로 조회된 게시물의 postId가 일치하는지 확인합니다.
             */

            //개수 일치 확인
            assertThat(postEntitiesOrderByLikeCntDesc.size()).isEqualTo(LIMIT);

            while (!postEntitiesOrderByLikeCntDesc.isEmpty()){
                PostEntity nowPost=postEntitiesOrderByLikeCntDesc.poll();
                assertThat(nowPost.getPostId()).isEqualTo(findPostEntities.get(idx).getPostId());
                assertThat(nowPost.getLikeCnt()).isEqualTo(findPostEntities.get(idx).getLikeCnt());
                idx++;
            }
        }
    }

    /*
    ======================================더미 데이터 생성 메소드======================================
     */
    private Member makeMember(){
        return Member.builder()
                .email("test@naver.com")
                .username("test")
                .build();
    }

    private PostEntity makePostEntity(Member member, HighlightEntity highlight,Long likeCnt,HashTag tag){
        return PostEntity.builder()
                .highlight(highlight)
                .content("content")
                .title("title")
                .member(member)
                .hashTag(tag)
                .likeCnt(likeCnt)
                .build();
    }

    private HighlightEntity makeHighlight(Member member){
        return HighlightEntity.builder()
                .highlightURL("testtest")
                .member(member)
                .highlightKey(UUID.randomUUID())
                .build();
    }

    /*
    ======================================초기 세팅 메소드======================================
     */
    private void set(
            PriorityQueue<PostEntity> postEntitiesOrderByCreatedAtDesc,
            PriorityQueue<PostEntity> postEntitiesOrderByLikeCntDesc
                     ) throws InterruptedException {
        member=memberRepository.save(makeMember());
        highlight=highlightCommandRepository.save(makeHighlight(member));
        Random random=new Random();

        //임의로 100개 데이터 삽입
        for (int i=0;i<LIMIT;i++){
            //1~1000 랜덤 좋아요 수ㅜ
            Long randomLikeCnt=random.nextLong(1000)+1L;
            HashTag hashTag=HashTag.TWO_POINT;
            PostEntity post=postCommandRepository.save(makePostEntity(member,highlight,randomLikeCnt,hashTag));

            postEntitiesOrderByCreatedAtDesc.add(post);
            postEntitiesOrderByLikeCntDesc.add(post);

            //1개씩 삽입 후 0.1초씩 기다림.
            Thread.sleep(100);
        }
    }

    private void clean(){
        postCommandRepository.deleteAll();
        highlightCommandRepository.deleteAll();
        memberRepository.deleteAll();
    }

}