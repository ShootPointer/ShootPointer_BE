package com.midas.shootpointer.domain.post.entity;

import com.midas.shootpointer.domain.backnumber.entity.BackNumber;
import com.midas.shootpointer.domain.backnumber.entity.BackNumberEntity;
import com.midas.shootpointer.domain.highlight.entity.HighlightEntity;
import org.assertj.core.api.Assertions;
import org.junit.Before;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

class PostEntityTest {

    @Test
    @DisplayName("새로운 게시물과 하이라이트 영상을 업데이트합니다.")
    void update() {
        //given
        UUID highlightKey1=UUID.randomUUID();
        UUID highlightKey2=UUID.randomUUID();
        HighlightEntity originalHighlight=mockHighlight(highlightKey1);
        HighlightEntity changedHighlight=mockHighlight(highlightKey2);

        PostEntity original=PostEntity.builder()
                .content("content1")
                .title("title1")
                .hashTag(HashTag.TREE_POINT)
                .highlight(originalHighlight)
                .build();
        String changedTitle="title2";
        String changedContent="content2";
        HashTag changedHashTag=HashTag.TWO_POINT;

        //when
        original.update(
                changedTitle,
                changedContent,
                changedHashTag,
                changedHighlight
        );

        //then
        assertThat(original.getTitle()).isEqualTo(changedTitle);
        assertThat(original.getHashTag()).isEqualTo(changedHashTag);
        assertThat(original.getContent()).isEqualTo(changedContent);
        assertThat(original.getHighlight()).isEqualTo(changedHighlight);

    }

    @Test
    @DisplayName("createLike() 실행 시 게시물의 좋아요 개수가 1씩 증가합니다.")
    void createLike(){
        //given
        String content="content_content";
        String title="title_title";

        PostEntity mockPost=PostEntity.builder()
                .content(content)
                .hashTag(HashTag.TREE_POINT)
                .title(title)
                .likeCnt(0)
                .build();

        //when
        mockPost.createLike();
        mockPost.createLike();

        //then
        assertThat(mockPost.getLikeCnt()).isEqualTo(2);
    }

    @Test
    @DisplayName("deleteLike() 실행 시 좋아요 개수가 0개 이하 시 게시물의 좋아요 개수가 0으로 유지됩니다.")
    void deleteLike_under_zero(){
        //given
        String content="content_content";
        String title="title_title";

        PostEntity mockPost=PostEntity.builder()
                .content(content)
                .hashTag(HashTag.TREE_POINT)
                .title(title)
                .likeCnt(1)
                .build();

        //when
        mockPost.deleteLike();
        mockPost.deleteLike();
        mockPost.deleteLike();
        mockPost.deleteLike();
        mockPost.deleteLike();

        //then
        assertThat(mockPost.getLikeCnt()).isEqualTo(0);
    }

    @Test
    @DisplayName("deleteLike() 실행 시 좋아요 개수가 1씩 감소합니다.")
    void deleteLike(){
        //given
        String content="content_content";
        String title="title_title";

        PostEntity mockPost=PostEntity.builder()
                .content(content)
                .hashTag(HashTag.TREE_POINT)
                .title(title)
                .likeCnt(10)
                .build();

        //when
        mockPost.deleteLike();
        mockPost.deleteLike();
        mockPost.deleteLike();
        mockPost.deleteLike();
        mockPost.deleteLike();

        //then
        assertThat(mockPost.getLikeCnt()).isEqualTo(5);
    }


    /**
     * mock Highlight
     */
    private HighlightEntity mockHighlight(UUID uuid){
        return HighlightEntity.builder()
                .highlightKey(uuid)
                .backNumber(
                        BackNumberEntity.builder()
                        .backNumber(BackNumber.of(10))
                        .build())
                .build();
    }
}