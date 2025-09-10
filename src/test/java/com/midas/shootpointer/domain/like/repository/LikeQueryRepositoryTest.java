package com.midas.shootpointer.domain.like.repository;

import com.midas.shootpointer.domain.backnumber.entity.BackNumber;
import com.midas.shootpointer.domain.backnumber.entity.BackNumberEntity;
import com.midas.shootpointer.domain.backnumber.repository.BackNumberRepository;
import com.midas.shootpointer.domain.highlight.entity.HighlightEntity;
import com.midas.shootpointer.domain.like.entity.LikeEntity;
import com.midas.shootpointer.domain.member.entity.Member;
import com.midas.shootpointer.domain.member.repository.MemberQueryRepository;
import com.midas.shootpointer.domain.memberbacknumber.repository.MemberBackNumberRepository;
import com.midas.shootpointer.domain.post.entity.HashTag;
import com.midas.shootpointer.domain.post.entity.PostEntity;
import com.midas.shootpointer.domain.post.repository.PostCommandRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
@DataJpaTest
@ActiveProfiles("dev")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class LikeQueryRepositoryTest {
    @Autowired
    private LikeQueryRepository likeQueryRepository;

    @Autowired
    private MemberQueryRepository memberRepository;

    @Autowired
    private PostCommandRepository postCommandRepository;

    @Autowired
    private LikeCommandRepository likeCommandRepository;


    @Test
    @DisplayName("멤버Id와 게시판Id로 좋아요가 존재하면 true를 반환합니다.")
    void existByMemberIdAndPostId_TRUE(){
        //given
        Member savedMember=memberRepository.save(makeMockMember());
        PostEntity savedPost=postCommandRepository.save(makeMockPost(savedMember));
        LikeEntity savedLike=likeCommandRepository.save(makeLike(savedMember,savedPost));

        //when
        boolean exist= likeQueryRepository.existByMemberIdAndPostId(savedMember.getMemberId(),savedPost.getPostId());

        //then
        assertThat(exist).isTrue();
    }

    @Test
    @DisplayName("멤버Id와 게시판Id로 좋아요가 존재하지 않으면 false를 반환합니다.")
    void existByMemberIdAndPostId_FALSE(){
        //given
        Member savedMember=memberRepository.save(makeMockMember());
        PostEntity savedPost=postCommandRepository.save(makeMockPost(savedMember));
        LikeEntity savedLike=likeCommandRepository.save(makeLike(savedMember,savedPost));
        UUID memberId=UUID.randomUUID();

        //when
        boolean exist= likeQueryRepository.existByMemberIdAndPostId(memberId,savedPost.getPostId());

        //then
        assertThat(exist).isFalse();
    }

    @Test
    @DisplayName("게시물Id와 유저Id로 좋아요를 조회합니다.")
    void findByPostIdAndMemberId(){
        //given
        Member savedMember=memberRepository.save(makeMockMember());
        PostEntity savedPost=postCommandRepository.save(makeMockPost(savedMember));
        LikeEntity savedLike=likeCommandRepository.save(makeLike(savedMember,savedPost));

        //when
        LikeEntity findLikeEntity=likeQueryRepository.findByPostIdAndMemberId(savedPost.getPostId(),savedMember.getMemberId())
                .orElseGet(null);

        //then
        assertThat(findLikeEntity).isNotNull();
        assertThat(findLikeEntity).isEqualTo(savedLike);
    }
    private Member makeMockMember(){
        return Member.builder()
                .username("tkv00")
                .email("test@naver.com")
                .build();
    }

    private PostEntity makeMockPost(Member member){
        return PostEntity.builder()
                .hashTag(HashTag.TREE_POINT)
                .content("content_content")
                .title("title_title")
                .member(member)
                .build();
    }

    private LikeEntity makeLike(Member member,PostEntity post){
        return LikeEntity.builder()
                .member(member)
                .post(post)
                .build();
    }

    private HighlightEntity makeHighlight(){
        return HighlightEntity.builder()
                .highlightURL("testest")
                .build();
    }
}