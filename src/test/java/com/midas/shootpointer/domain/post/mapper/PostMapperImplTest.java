package com.midas.shootpointer.domain.post.mapper;

import com.midas.shootpointer.domain.highlight.entity.HighlightEntity;
import com.midas.shootpointer.domain.highlight.repository.HighlightCommandRepository;
import com.midas.shootpointer.domain.member.entity.Member;
import com.midas.shootpointer.domain.member.repository.MemberRepository;
import com.midas.shootpointer.domain.post.dto.PostRequest;
import com.midas.shootpointer.domain.post.dto.PostResponse;
import com.midas.shootpointer.domain.post.entity.HashTag;
import com.midas.shootpointer.domain.post.entity.PostEntity;
import com.midas.shootpointer.domain.post.repository.PostCommandRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
@SpringBootTest
@ActiveProfiles("dev")
class PostMapperImplTest {
    @Autowired
    private PostMapperImpl postMapper;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private PostCommandRepository postCommandRepository;

    @Autowired
    private HighlightCommandRepository highlightCommandRepository;

    @Test
    @DisplayName("Dto를 Entity로 매핑합니다.")
    void dtoToEntity_SUCCESS(){
        //given
        String content="test_test_test_test_test_test_test_test_test_test_test_test_";
        String title="title";
        HashTag hashTag=HashTag.TREE_POINT;
        UUID highlightId=UUID.randomUUID();
        PostRequest request=PostRequest.of(highlightId,title,content,hashTag);
        Member mock=makeMockMember();


        //when
        PostEntity postEntity=postMapper.dtoToEntity(request,mock);

        //then
        assertThat(postEntity.getContent()).isEqualTo(content);
        assertThat(postEntity.getHashTag()).isEqualTo(hashTag);
        assertThat(postEntity.getTitle()).isEqualTo(title);
        assertThat(postEntity.getMember()).isEqualTo(mock);
    }

    @Test
    @DisplayName("Entity를 Dto로 매핑합니다.")
    void entityToDto_SUCCESS(){
        //given
        String content="test_test_test_test_test_test_test_test_test_test_test_test_";
        String title="title";

        Member mock=memberRepository.save(makeMockMember());
        HighlightEntity highlight=highlightCommandRepository.save(HighlightEntity.builder()
                .highlightKey(UUID.randomUUID())
                .member(mock)
                .highlightURL("testst")
                .build());

        PostEntity postEntity=PostEntity.builder()
                .title(title)
                .content(content)
                .highlight(highlight)
                .hashTag(HashTag.TREE_POINT)
                .member(mock)
                .build();



        //when
        PostResponse postResponse=postMapper.entityToDto(postEntity);

        //then
        assertThat(postResponse.getContent()).isEqualTo(postEntity.getContent());
        assertThat(postResponse.getHashTag()).isEqualTo(postEntity.getHashTag());
        assertThat(postResponse.getHighlightUrl()).isEqualTo(postEntity.getHighlight().getHighlightURL());
        assertThat(postResponse.getTitle()).isEqualTo(postEntity.getTitle());
        assertThat(postResponse.getLikeCnt()).isEqualTo(postEntity.getLikeCnt());
        assertThat(postResponse.getPostId()).isEqualTo(postEntity.getPostId());
        assertThat(postResponse.getCreatedAt()).isEqualTo(postEntity.getCreatedAt());
        assertThat(postResponse.getModifiedAt()).isEqualTo(postEntity.getModifiedAt());
    }

    /**
     * Mock Member
     */
    private Member makeMockMember(){
        return Member.builder()
                .email("test@naver.com")
                .username("teest")
                .build();
    }

}