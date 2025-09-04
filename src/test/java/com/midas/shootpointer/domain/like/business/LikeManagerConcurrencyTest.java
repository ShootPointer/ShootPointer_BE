package com.midas.shootpointer.domain.like.business;

import com.midas.shootpointer.domain.like.repository.LikeCommandRepository;
import com.midas.shootpointer.domain.member.entity.Member;
import com.midas.shootpointer.domain.member.repository.MemberRepository;
import com.midas.shootpointer.domain.post.entity.HashTag;
import com.midas.shootpointer.domain.post.entity.PostEntity;
import com.midas.shootpointer.domain.post.repository.PostCommandRepository;
import com.midas.shootpointer.domain.post.repository.PostQueryRepository;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

import static org.assertj.core.api.Assertions.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@SpringBootTest
class LikeManagerConcurrencyTest {
    @Autowired
    private LikeManager likeManager;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private PostCommandRepository postCommandRepository;

    @Autowired
    private PostQueryRepository postQueryRepository;

    @Autowired
    private LikeCommandRepository likeCommandRepository;

    /**
     * 좋아요할 게시물 Id
     */
    private static Long postId;
    private static final List<Member> memberList = new ArrayList<>();

    @BeforeAll
    void initData() {
        // 게시물 하나 생성
        Member owner = memberRepository.saveAndFlush(makeMockMember());
        postId = postCommandRepository.saveAndFlush(makeMockPost(owner)).getPostId();

        // 10,000 명 생성
        List<Member> bulk = new ArrayList<>();
        for (int i=0; i<10_000; i++) {
            bulk.add(makeMockMember());
            if (i % 1_000 == 0) {
                memberRepository.saveAllAndFlush(bulk);
                memberList.addAll(bulk);
                bulk.clear();
            }
        }
        if (!bulk.isEmpty()) {
            memberRepository.saveAllAndFlush(bulk);
            memberList.addAll(bulk);
        }
    }
    @AfterEach
    void cleanLikes() {
        likeCommandRepository.deleteAll();
        postQueryRepository.findByPostId(postId).ifPresent(post -> {
            post.setLikeCnt(0);
            /**
             * atomic
             */
            post.setAtomicLikeCnt(new AtomicInteger(0));
            postCommandRepository.saveAndFlush(post);
        });
    }

    @AfterAll
    void clearData() {
        likeCommandRepository.deleteAll();
        postCommandRepository.deleteAll();
        memberRepository.deleteAll();
    }

    private final int INF_0=100;
    private final int INF_1 = 1_000;
    private final int INF_2 = 10_000;

/*    @Test
    @DisplayName("incrementQuery - 동시에 100개의 요청으로 좋아요 수를 증가시킵니다.")
    void incrementQuery_increase_100_request_of_like() throws InterruptedException {
        extracted(INF_0);
    }

    @Test
    @DisplayName("incrementQuery - 동시에 1,000개의 요청으로 좋아요 수를 증가시킵니다.")
    void incrementQuery_increase_1_000_request_of_like() throws InterruptedException {
        extracted(INF_1);
    }

    @Test
    @DisplayName("incrementQuery - 동시에 10,000개의 요청으로 좋아요 수를 증가시킵니다.")
    void incrementQuery_increase_10_000_request_of_like() throws InterruptedException {
        extracted(INF_2);
    }*/

    @Test
    @DisplayName("AtomicInteger 사용 -동시에 100개의 요청으로 좋아요 수를 증가시킵니다.")
    void AtomicInteger_increase_100_request_of_like() throws InterruptedException {
        atomicExtracted(INF_0);
    }

    @Test
    @DisplayName("AtomicInteger 사용 -동시에 1,000개의 요청으로 좋아요 수를 증가시킵니다.")
    void AtomicInteger_increase_1_000_request_of_like() throws InterruptedException {
        atomicExtracted(INF_1);
    }

    @Test
    @DisplayName("AtomicInteger 사용 -동시에 10,000개의 요청으로 좋아요 수를 증가시킵니다.")
    void AtomicInteger_increase_10_000_request_of_like() throws InterruptedException {
        atomicExtracted(INF_2);
    }



    private void extracted(int threadCnt) throws InterruptedException {
        //given
        final ExecutorService executorService = Executors.newFixedThreadPool(32);
        final CountDownLatch countDownLatch = new CountDownLatch(threadCnt);


        //when
        for (int i = 0; i < threadCnt; i++) {
            final int idx = i;
            executorService.submit(() -> {
                try {
                    likeManager.increase(postId, memberList.get(idx));
                } finally {
                    countDownLatch.countDown();
                }
            });
        }
        countDownLatch.await();
        final PostEntity post = postQueryRepository.findByPostId(postId).orElseThrow();

        //then
        assertThat(post.getLikeCnt()).isEqualTo(threadCnt);
    }

    private void atomicExtracted(int threadCnt) throws InterruptedException {
        //given
        final ExecutorService executorService = Executors.newFixedThreadPool(32);
        final CountDownLatch countDownLatch = new CountDownLatch(threadCnt);
        final PostEntity post = postQueryRepository.findByPostId(postId).orElseThrow();


        //when
        for (int i = 0; i < threadCnt; i++) {
            final int idx = i;
            executorService.submit(() -> {
                try {
                    post.increaseAtomicLikeCnt();
                } finally {
                    countDownLatch.countDown();
                }
            });
        }
        countDownLatch.await();

        //then
        assertThat(post.getAtomicLikeCnt().get()).isEqualTo(threadCnt);
    }

    private Member makeMockMember() {
        String random = UUID.randomUUID().toString().substring(0, 5);
        return Member.builder()
                .username("test" + random)
                .email("test" + random + "@naver.com")
                .build();
    }

    private PostEntity makeMockPost(Member member) {
        return PostEntity.builder()
                .member(member)
                .hashTag(HashTag.TREE_POINT)
                .content("content")
                .title("title")
                .build();
    }


}