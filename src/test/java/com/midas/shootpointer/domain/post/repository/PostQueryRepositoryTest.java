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