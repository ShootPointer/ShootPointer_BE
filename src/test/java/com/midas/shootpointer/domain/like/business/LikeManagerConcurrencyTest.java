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
            if (i % 1000 == 0) {
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

    @Test
    @DisplayName("동시에 100개의 요청으로 좋아요 수를 증가시킵니다.")
    void increase_100_request_of_like() throws InterruptedException {
        extracted(INF_0);
    }

    @Test
    @DisplayName("동시에 1_000개의 요청으로 좋아요 수를 증가시킵니다.")
    void increase_1_000_request_of_like() throws InterruptedException {
        //given
        extracted(INF_1);
    }

    @Test
    @DisplayName("동시에 10_000개의 요청으로 좋아요 수를 증가시킵니다.")
    void increase_10_000_request_of_like() throws InterruptedException {
        //given
        extracted(INF_2);
    }


    private void extracted(int INF_0) throws InterruptedException {
        //given
        final int threadCount = INF_0;
        final ExecutorService executorService = Executors.newFixedThreadPool(32);
        final CountDownLatch countDownLatch = new CountDownLatch(threadCount);


        //when
        for (int i = 0; i < threadCount; i++) {
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
        assertThat(post.getLikeCnt()).isEqualTo(INF_0);
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