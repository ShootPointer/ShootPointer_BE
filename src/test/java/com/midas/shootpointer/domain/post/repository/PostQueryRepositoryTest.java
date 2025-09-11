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

import java.util.List;
import java.util.PriorityQueue;
import java.util.Random;
import java.util.UUID;

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



    private Member member;
    private HighlightEntity highlight;

    private static final int LIMIT=100;

    /*
    ======================================대용량 데이터 사용 쿼리 테스트======================================
     */

    @Nested
    class PostQueryRepositoryTestForBulk{
        private static PriorityQueue<PostEntity> postEntitiesOrderByCreatedAtDesc=new PriorityQueue<>((a,b)->b.getCreatedAt().compareTo(a.getCreatedAt()));

        @BeforeEach
        void setUp() throws InterruptedException {
            set(postEntitiesOrderByCreatedAtDesc);
        }

        @AfterEach
        void delete(){
            clean();
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
            while (!postEntitiesOrderByCreatedAtDesc.isEmpty()){
                PostEntity nowPost=postEntitiesOrderByCreatedAtDesc.poll();
                assertThat(nowPost.getPostId()).isEqualTo(findPostEntities.get(idx).getPostId());
                assertThat(nowPost.getCreatedAt()).isEqualTo(findPostEntities.get(idx).getCreatedAt());
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
            while (!postEntitiesOrderByCreatedAtDesc.isEmpty()){
                PostEntity nowPost=postEntitiesOrderByCreatedAtDesc.poll();
                assertThat(nowPost.getPostId()).isEqualTo(findPostEntities.get(idx).getPostId());
                assertThat(nowPost.getCreatedAt()).isEqualTo(findPostEntities.get(idx).getCreatedAt());
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
    private void set(PriorityQueue<PostEntity> postEntities) throws InterruptedException {
        member=memberRepository.save(makeMember());
        highlight=highlightCommandRepository.save(makeHighlight(member));
        Random random=new Random();

        //임의로 100개 데이터 삽입
        for (int i=0;i<LIMIT;i++){
            //1~1000 랜덤 좋아요 수ㅜ
            Long randomLikeCnt=random.nextLong(1000)+1L;
            HashTag hashTag=HashTag.TWO_POINT;
            PostEntity post=postCommandRepository.save(makePostEntity(member,highlight,randomLikeCnt,hashTag));

            postEntities.add(post);

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