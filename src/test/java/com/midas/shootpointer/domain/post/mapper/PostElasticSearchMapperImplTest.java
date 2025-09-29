package com.midas.shootpointer.domain.post.mapper;

import com.midas.shootpointer.domain.highlight.entity.HighlightEntity;
import com.midas.shootpointer.domain.member.entity.Member;
import com.midas.shootpointer.domain.post.entity.HashTag;
import com.midas.shootpointer.domain.post.entity.PostDocument;
import com.midas.shootpointer.domain.post.entity.PostEntity;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PostElasticSearchMapperImplTest {
    @InjectMocks
    private PostElasticSearchMapperImpl mapper;

    @Mock
    private Member member;

    @Mock
    private HighlightEntity highlight;


    @Test
    @DisplayName("PostEntity를 PostDocument 형태로 변환합니다.")
    void entityToDoc() {
        //given
        LocalDateTime now=LocalDateTime.now();
        PostEntity post = PostEntity.builder()
                .postId(10L)
                .content("content")
                .hashTag(HashTag.TREE_POINT)
                .highlight(highlight)
                .likeCnt(10L)
                .member(member)
                .title("title")
                .build();
        post.setCreatedAt(now);
        post.setModifiedAt(now);

        //when
        when(member.getUsername()).thenReturn("test");
        when(highlight.getHighlightURL()).thenReturn("url");


        PostDocument postDocument=mapper.entityToDoc(post);

        //then
        assertThat(postDocument.getContent()).isEqualTo(post.getContent());
        assertThat(postDocument.getCreatedAt()).isEqualTo(post.getCreatedAt());
        assertThat(postDocument.getModifiedAt()).isEqualTo(post.getModifiedAt());
        assertThat(postDocument.getHashTag()).isEqualTo(post.getHashTag().getName());
        assertThat(postDocument.getLikeCnt()).isEqualTo(post.getLikeCnt());
        assertThat(postDocument.getMemberName()).isEqualTo(post.getMember().getUsername());
        assertThat(postDocument.getPostId()).isEqualTo(post.getPostId());
        assertThat(postDocument.getHighlightUrl()).isEqualTo(post.getHighlight().getHighlightURL());
        assertThat(postDocument.getTitle()).isEqualTo(post.getTitle());
    }
}