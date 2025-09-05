package com.midas.shootpointer.domain.post.helper;

import com.midas.shootpointer.domain.member.entity.Member;
import com.midas.shootpointer.domain.member.repository.MemberQueryRepository;
import com.midas.shootpointer.domain.post.entity.HashTag;
import com.midas.shootpointer.domain.post.entity.PostEntity;
import com.midas.shootpointer.domain.post.repository.PostCommandRepository;
import com.midas.shootpointer.domain.post.repository.PostQueryRepository;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * 통합 테스트로 진행.
 */
@SpringBootTest
@ActiveProfiles("test")
class PostUtilImplTest {
    @Autowired
    private PostUtilImpl postUtil;

    @Autowired
    private PostQueryRepository postQueryRepository;

    @Autowired
    private MemberQueryRepository memberRepository;

    @Autowired
    private PostCommandRepository postCommandRepository;

    @Test
    @DisplayName("게시판 Id를 이용하여 게시물을 조회합니다._SUCCESS")
    void findPostByPostId() {
        //given
        Member member=memberRepository.save(makeMember());
        PostEntity post=postCommandRepository.save(makeMockPost(member));

        //when
        PostEntity findPost=postUtil.findPostByPostId(post.getPostId());

        //then
        assertThat(findPost.getPostId()).isEqualTo(post.getPostId());
        assertThat(findPost.getMember().getMemberId()).isEqualTo(member.getMemberId());
    }

    @Test
    @DisplayName("게시판 객체를 저장합니다._SUCCESS")
    void save() {
        //given
        Member member=memberRepository.save(makeMember());
        PostEntity post=makeMockPost(member);

        //when
        PostEntity savedPost=postUtil.save(post);

        //then
        assertThat(post.getPostId()).isEqualTo(savedPost.getPostId());
        assertThat(post.getMember().getMemberId()).isEqualTo(savedPost.getMember().getMemberId());
    }

    @Test
    @DisplayName("게시판 Id를 이용하여 게시물을 조회합니다.-Pessimistic Lock 적용_SUCCESS")
    void findByPostId_with_pessimisticLock(){
        //given
        Member member=memberRepository.save(makeMember());
        PostEntity post=postCommandRepository.save(makeMockPost(member));

        //when
        PostEntity findPost=postUtil.findByPostByPostIdWithPessimisticLock(post.getPostId());

        //then
        assertThat(findPost.getPostId()).isEqualTo(post.getPostId());
        assertThat(findPost.getMember().getMemberId()).isEqualTo(member.getMemberId());
    }

    private PostEntity makeMockPost(Member member){
        return PostEntity.builder()
                .title("title")
                .member(member)
                .content("content")
                .hashTag(HashTag.TREE_POINT)
                .build();
    }

    private Member makeMember(){
        return Member.builder()
                .email("test@naver.com")
                .username("tet")
                .build();
    }

}