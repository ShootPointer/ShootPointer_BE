package com.midas.shootpointer.domain.like.business;

import com.midas.shootpointer.domain.like.entity.LikeEntity;
import com.midas.shootpointer.domain.like.repository.LikeCommandRepository;
import com.midas.shootpointer.domain.member.entity.Member;
import com.midas.shootpointer.domain.member.repository.MemberQueryRepository;
import com.midas.shootpointer.domain.post.entity.HashTag;
import com.midas.shootpointer.domain.post.entity.PostEntity;
import com.midas.shootpointer.domain.post.repository.PostCommandRepository;
import com.midas.shootpointer.domain.post.repository.PostQueryRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("dev")
@Transactional
/**
 * 통합 테스트로 진행합니다.
 */
class LikeManagerTest  {
    @BeforeEach
    void setUp(){
        memberRepository.deleteAll();
        postCommandRepository.deleteAll();
        likeCommandRepository.deleteAll();
    }

    @AfterEach
    void cleanUp(){
        likeCommandRepository.deleteAll();;
        postCommandRepository.deleteAll();
        memberRepository.deleteAll();
    }

    @Autowired
    private LikeManager likeManager;

    @Autowired
    private MemberQueryRepository memberRepository;

    @Autowired
    private PostCommandRepository postCommandRepository;

    @Autowired
    private PostQueryRepository postQueryRepository;

    @Autowired
    private LikeCommandRepository likeCommandRepository;

    @Test
    @DisplayName("postHelper와 likeHelper의 유효성을 검증한 후 좋아요 객체를 생성하고 저장합니다.")
    void increase() {
        //given
        Member savedMember_1=memberRepository.save(makeMockMember());
        Member savedMember_2=memberRepository.save(makeMockMember());
        Member savedMember_3=memberRepository.save(makeMockMember());
        Member savedMember_4=memberRepository.save(makeMockMember());
        PostEntity savedPost=postCommandRepository.save(makePostEntity(savedMember_1));

        //when
        Long likeId_1=likeManager.increase(savedPost.getPostId(),savedMember_1);
        Long likeId_2=likeManager.increase(savedPost.getPostId(),savedMember_2);
        Long likeId_3=likeManager.increase(savedPost.getPostId(),savedMember_3);
        Long likeId_4=likeManager.increase(savedPost.getPostId(),savedMember_4);
        PostEntity findPost=postQueryRepository.findByPostId(savedPost.getPostId()).orElseThrow();

        //then
        assertThat(findPost.getLikeCnt()).isEqualTo(4);

    }


    @Test
    @DisplayName("postHelper와 likeHelper의 유효성을 검증한 후 좋아요 객체를 삭제 및 감소 시킵니다.")
    void decrease() {
        //given
        Member savedMember_1=memberRepository.save(makeMockMember());
        Member savedMember_2=memberRepository.save(makeMockMember());
        PostEntity savedPost=postCommandRepository.save(makePostEntity(savedMember_1));

        savedPost.setLikeCnt(5L);

        LikeEntity likeEntity_1=makeMockLike(savedPost,savedMember_1);
        LikeEntity likeEntity_2=makeMockLike(savedPost,savedMember_2);
        likeCommandRepository.saveAll(List.of(likeEntity_1,likeEntity_2));

        /**
         * 좋아요 처음 개수 = 5
         */

        /**
         * 좋아요 2개 감소
         */
        likeManager.decrease(savedPost.getPostId(),savedMember_1);
        likeManager.decrease(savedPost.getPostId(),savedMember_2);

        PostEntity findPost=postQueryRepository.findByPostId(savedPost.getPostId()).orElseThrow();

        //then
        assertThat(findPost.getLikeCnt()).isEqualTo(3);
    }

    private LikeEntity makeMockLike(PostEntity post,Member member){
        return LikeEntity.builder()
                .post(post)
                .member(member)
                .build();
    }

    private Member makeMockMember(){
        return Member.builder()
                .username("user")
                .email("test@naver.com")
                .build();
    }

    private PostEntity makePostEntity(Member member){
        return PostEntity.builder()
                .hashTag(HashTag.TREE_POINT)
                .content("content")
                .title("title")
                .member(member)
                .build();
    }
}