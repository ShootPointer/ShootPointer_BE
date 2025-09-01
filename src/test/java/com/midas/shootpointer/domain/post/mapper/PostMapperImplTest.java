package com.midas.shootpointer.domain.post.mapper;

import com.midas.shootpointer.domain.member.entity.Member;
import com.midas.shootpointer.domain.post.dto.PostRequest;
import com.midas.shootpointer.domain.post.entity.HashTag;
import com.midas.shootpointer.domain.post.entity.PostEntity;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
@SpringBootTest
@ActiveProfiles("test")
class PostMapperImplTest {
    @Autowired
    private PostMapperImpl postMapper;

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

    /**
     * Mock Member
     */
    private Member makeMockMember(){
        return Member.builder()
                .email("test@naver.com")
                .memberId(UUID.randomUUID())
                .username("teest")
                .build();
    }

}