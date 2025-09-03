package com.midas.shootpointer.domain.like.business;

import com.midas.shootpointer.domain.like.helper.LikeHelper;
import com.midas.shootpointer.domain.like.repository.LikeCommandRepository;
import com.midas.shootpointer.domain.member.entity.Member;
import com.midas.shootpointer.domain.member.repository.MemberRepository;
import com.midas.shootpointer.domain.post.entity.HashTag;
import com.midas.shootpointer.domain.post.entity.PostEntity;
import com.midas.shootpointer.domain.post.helper.PostHelper;
import com.midas.shootpointer.domain.post.repository.PostCommandRepository;
import com.midas.shootpointer.domain.post.repository.PostQueryRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;
@SpringBootTest
@ActiveProfiles("test")
class LikeManagerConcurrencyTest {
    @Autowired
    private LikeManager likeManager;

    @Autowired
    private LikeHelper likeHelper;

    @Autowired
    private PostHelper postHelper;

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
    private static final List<Member> memberList=new ArrayList<>();

    @BeforeEach
    void setUp(){
        /**
         * 더미 게시판 작성.
         */
        Member savedMember=memberRepository.saveAndFlush(makeMockMember());
        postId=postCommandRepository.saveAndFlush(makeMockPost(savedMember)).getPostId();

        /**
         * 더미 멤버 삽입
         */
        for (int i=0;i<INF;i++){
            memberList.add(memberRepository.save(makeMockMember()));
        }
    }

    @AfterEach
    void tearDown(){
        memberRepository.deleteAll();;
        postCommandRepository.deleteAll();
        likeCommandRepository.deleteAll();
    }

    private final int INF=10000;
    @Test
    @DisplayName("동시에 10000개의 요청으로 좋아요 수를 증가시킵니다.")
    void increate_10000_request_of_like() throws InterruptedException {
        //given
        final int threadCount=INF;
        final ExecutorService executorService= Executors.newFixedThreadPool(32);
        final CountDownLatch countDownLatch=new CountDownLatch(threadCount);


        //when
        for (int i=0;i<threadCount;i++){
            final int idx=i;
            executorService.submit(()->{
                try {
                    likeManager.increase(postId,memberList.get(idx));
                }finally {
                    countDownLatch.countDown();
                }
            });
        }
        countDownLatch.await();
        final PostEntity post=postQueryRepository.findByPostId(postId).orElseThrow();

        //then
        assertThat(post.getLikeCnt()).isEqualTo(INF);
    }

    private Member makeMockMember(){
        String random=UUID.randomUUID().toString().substring(0,5);
        return Member.builder()
                .username("test"+random)
                .email("test"+random+"@naver.com")
                .build();
    }

    private PostEntity makeMockPost(Member member){
        return PostEntity.builder()
                .member(member)
                .hashTag(HashTag.TREE_POINT)
                .content("content")
                .title("title")
                .build();
    }


}