package com.midas.shootpointer.domain.post.helper;

import com.midas.shootpointer.domain.backnumber.entity.BackNumberEntity;
import com.midas.shootpointer.domain.highlight.entity.HighlightEntity;
import com.midas.shootpointer.domain.highlight.repository.HighlightCommandRepository;
import com.midas.shootpointer.domain.member.entity.Member;
import com.midas.shootpointer.domain.member.repository.MemberRepository;
import com.midas.shootpointer.domain.post.entity.HashTag;
import com.midas.shootpointer.domain.post.entity.PostEntity;
import com.midas.shootpointer.domain.post.repository.PostCommandRepository;
import com.midas.shootpointer.domain.post.repository.PostQueryRepository;
import com.midas.shootpointer.global.common.ErrorCode;
import com.midas.shootpointer.global.exception.CustomException;
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
import java.util.UUID;

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
    private MemberRepository memberRepository;

    @Autowired
    private PostCommandRepository postCommandRepository;

    @Autowired
    private PostQueryRepository postQueryRepository;

    @Autowired
    private HighlightCommandRepository highlightCommandRepository;


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
    @DisplayName("게시판 Id로 게시판 객체를 조회합니다._SUCCESS")
    void find_SUCCESS(){
        //given
        Member member=memberRepository.save(makeMember());
        PostEntity post=postCommandRepository.save(makeMockPost(member));
        Long postId=post.getPostId();

        //when
        PostEntity findPost=postUtil.findPostByPostId(postId);

        //then
        assertThat(findPost).isNotNull();
        assertThat(findPost.getMember().getMemberId()).isEqualTo(post.getMember().getMemberId());
        assertThat(findPost.getPostId()).isEqualTo(postId);
        assertThat(findPost.getContent()).isEqualTo(post.getContent());
        assertThat(findPost.getHashTag()).isEqualTo(post.getHashTag());
    }


    @Test
    @DisplayName("게시판 Id로 게시판 객체를 조회를 실패하면 IS_NOT_EXIST_POST 예외를 발생시킵니다._FAIL")
    void find_FAIL(){
        //given
        Long postId=123L;

        //when
        CustomException exception=catchThrowableOfType(()->
                        postUtil.findPostByPostId(postId),
                        CustomException.class
        );

        //then
        assertThat(exception).isNotNull();
        assertThat(exception.getErrorCode()).isEqualTo(ErrorCode.IS_NOT_EXIST_POST);
    }

    @Test
    @DisplayName("게시판을 수정합니다._SUCCESS")
    void update_SUCCESS() {
        //given
        Member member = memberRepository.save(makeMember());
        PostEntity post = postCommandRepository.save(makeMockPost(member));
        HighlightEntity highlightEntity = highlightCommandRepository.save(makeHighlight(member));

        PostEntity newPost = PostEntity.builder()
                .title("title2")
                .member(member)
                .content("content2")
                .highlight(highlightEntity)
                .hashTag(HashTag.TWO_POINT)
                .build();

        //when
        PostEntity updatedPost = postUtil.update(newPost, post, highlightEntity);

        //then
        assertThat(updatedPost.getHashTag()).isEqualTo(newPost.getHashTag());
        assertThat(updatedPost.getContent()).isEqualTo(newPost.getContent());
        assertThat(updatedPost.getPostId()).isEqualTo(post.getPostId());
        assertThat(updatedPost.getMember().getMemberId()).isEqualTo(post.getMember().getMemberId());
        assertThat(updatedPost.getTitle()).isEqualTo(post.getTitle());
    }

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

    private HighlightEntity makeHighlight(Member member){
        return HighlightEntity.builder()
                .highlightKey(UUID.randomUUID())
                .member(member)
                .highlightURL("test")
                .build();
    }

}