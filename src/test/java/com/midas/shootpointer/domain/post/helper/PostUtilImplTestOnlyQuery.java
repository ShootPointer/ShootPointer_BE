package com.midas.shootpointer.domain.post.helper;

import com.midas.shootpointer.domain.highlight.entity.HighlightEntity;
import com.midas.shootpointer.domain.member.entity.Member;
import com.midas.shootpointer.domain.post.entity.HashTag;
import com.midas.shootpointer.domain.post.entity.PostEntity;
import com.midas.shootpointer.domain.post.repository.PostQueryRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

/*
    ======================================단순 레포지토리 쿼리 사용 테스트(통합테스트가 아닌 Mock를 활용한 단위테스트 진행)======================================
     */
@ExtendWith(MockitoExtension.class)
public class PostUtilImplTestOnlyQuery {
    @InjectMocks
    private PostUtilImpl postUtil;

    @Mock
    private PostQueryRepository postQueryRepository;

    @DisplayName("게시물을 최신순 10개 조회합니다. - postQueryRepository-getLatestPostListBySliceAndNoOffset() 동작 유무를 확인합니다.")
    @Test
    void getLatestPostListBySliceAndNoOffset() {
        // given
        Long postId = 21312313213L;
        int size = 10;
        List<PostEntity> postEntities = new ArrayList<>();
        Member mockMember = makeMember();
        for (int i = 0; i < 10; i++) {
            postEntities.add(makeMockPost(mockMember, makeHighlight(mockMember)));
        }

        when(postQueryRepository.getLatestPostListBySliceAndNoOffset(anyLong(), anyInt()))
                .thenReturn(postEntities);

        // when
        List<PostEntity> result = postUtil.getLatestPostListBySliceAndNoOffset(postId, size);

        // then
        assertThat(result).hasSize(10);
        verify(postQueryRepository, times(1)).getLatestPostListBySliceAndNoOffset(anyLong(), anyInt());
    }

    @DisplayName("게시물을 좋아요순 10개 조회합니다. - postQueryRepository-getPopularPostListBySliceAndNoOffset() 동작 유무를 확인합니다.")
    @Test
    void getPopularPostListBySliceAndNoOffset() {
        // given
        Long likeCnt = 21312313213L;
        int size = 10;
        List<PostEntity> postEntities = new ArrayList<>();
        Member mockMember = makeMember();
        for (int i = 0; i < 10; i++) {
            postEntities.add(makeMockPost(mockMember, makeHighlight(mockMember)));
        }

        when(postQueryRepository.getPopularPostListBySliceAndNoOffset(anyInt(), anyLong()))
                .thenReturn(postEntities);

        // when
        List<PostEntity> result = postUtil.getPopularPostListBySliceAndNoOffset(likeCnt,size);

        // then
        assertThat(result).hasSize(10);
        verify(postQueryRepository, times(1)).getPopularPostListBySliceAndNoOffset(anyInt(), anyLong());
    }



    private PostEntity makeMockPost(Member member, HighlightEntity highlight) {
        return PostEntity.builder()
                .title("title")
                .member(member)
                .highlight(highlight)
                .content("content")
                .hashTag(HashTag.TREE_POINT)
                .build();
    }

    private Member makeMember() {
        return Member.builder()
                .email("test@naver.com")
                .username("tet")
                .build();
    }

    private HighlightEntity makeHighlight(Member member) {
        return HighlightEntity.builder()
                .highlightKey(UUID.randomUUID())
                .member(member)
                .highlightURL("test")
                .build();
    }
}
