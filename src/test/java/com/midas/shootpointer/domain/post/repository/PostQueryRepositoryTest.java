package com.midas.shootpointer.domain.post.repository;

import com.midas.shootpointer.domain.highlight.entity.HighlightEntity;
import com.midas.shootpointer.domain.highlight.repository.HighlightCommandRepository;
import com.midas.shootpointer.domain.member.entity.Member;
import com.midas.shootpointer.domain.member.repository.MemberRepository;
import com.midas.shootpointer.domain.post.dto.PostResponse;
import com.midas.shootpointer.domain.post.entity.HashTag;
import com.midas.shootpointer.domain.post.entity.PostEntity;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.UUID;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;
@SpringBootTest
@ActiveProfiles("dev")
class PostQueryRepositoryTest {
    @Autowired
    private PostQueryRepository postQueryRepository;

    @Autowired
    private PostCommandRepository postCommandRepository;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private HighlightCommandRepository highlightCommandRepository;


    @Test
    @DisplayName("게시물 id를 이용하여 게시물 응답 dto 조립 후 반환합니다.")
    void findPostResponseDtoByPostId(){
        //given
        Member member=memberRepository.save(makeMember());
        HighlightEntity highlight=highlightCommandRepository.save(makeHighlight(member));
        PostEntity post=postCommandRepository.save(makePostEntity(member,highlight));

        PostResponse expectedResponse=PostResponse.builder()
                .content("content")
                .createdAt(post.getCreatedAt())
                .hashTag(post.getHashTag())
                .highlightUrl(post.getHighlight().getHighlightURL())
                .likeCnt(post.getLikeCnt())
                .modifiedAt(post.getModifiedAt())
                .postId(post.getPostId())
                .title(post.getTitle())
                .build();

        //when
        PostResponse findResponse=postQueryRepository.findPostResponseDtoByPostId(post.getPostId())
                .orElse(null);

        //then
        assertNotNull(findResponse);
        assertThat(findResponse.getModifiedAt()).isEqualTo(expectedResponse.getModifiedAt());
        assertThat(findResponse.getCreatedAt()).isEqualTo(expectedResponse.getCreatedAt());
        assertThat(findResponse.getPostId()).isEqualTo(expectedResponse.getPostId());
        assertThat(findResponse.getContent()).isEqualTo(expectedResponse.getContent());
        assertThat(findResponse.getHashTag()).isEqualTo(expectedResponse.getHashTag());
        assertThat(findResponse.getTitle()).isEqualTo(expectedResponse.getTitle());
        assertThat(findResponse.getLikeCnt()).isEqualTo(expectedResponse.getLikeCnt());
        assertThat(findResponse.getHighlightUrl()).isEqualTo(expectedResponse.getHighlightUrl());
    }

    private Member makeMember(){
        return Member.builder()
                .email("test@naver.com")
                .username("test")
                .build();
    }

    private PostEntity makePostEntity(Member member, HighlightEntity highlight){
        return PostEntity.builder()
                .highlight(highlight)
                .content("content")
                .title("title")
                .member(member)
                .hashTag(HashTag.TWO_POINT)
                .build();
    }

    private HighlightEntity makeHighlight(Member member){
        return HighlightEntity.builder()
                .highlightURL("testtest")
                .member(member)
                .highlightKey(UUID.randomUUID())
                .build();
    }
}