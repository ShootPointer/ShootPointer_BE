package com.midas.shootpointer.domain.post.business;

import com.midas.shootpointer.domain.highlight.entity.HighlightEntity;
import com.midas.shootpointer.domain.highlight.helper.HighlightHelper;
import com.midas.shootpointer.domain.member.entity.Member;
import com.midas.shootpointer.domain.post.business.command.PostCommandService;
import com.midas.shootpointer.domain.post.business.command.PostCommandServiceImpl;
import com.midas.shootpointer.domain.post.dto.PostRequest;
import com.midas.shootpointer.domain.post.entity.HashTag;
import com.midas.shootpointer.domain.post.entity.PostEntity;
import com.midas.shootpointer.domain.post.helper.PostHelper;
import com.midas.shootpointer.domain.post.mapper.PostMapper;
import com.midas.shootpointer.domain.post.repository.PostCommandRepository;
import com.midas.shootpointer.domain.post.repository.PostQueryRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PostManagerTest {
    @InjectMocks
    private PostManager postManager;

    @Mock
    private PostHelper postHelper;

    @Mock
    private HighlightHelper highlightHelper;

    @Mock
    private PostCommandRepository postCommandRepository;

    @Mock
    private PostQueryRepository postQueryRepository;

    @Test
    @DisplayName(
            "Highlight URL이 유저의 영상으로 일치 여부를 검증하고 " +
            "해시태그가 올바른 지 여부를 확인하고 게시물을 저장하고 " +
            "반환값으로 게시물의 id를 반환합니다."
    )
    void save() {
        //given
        Member mockMember=mockMember();
        PostEntity mockPostEntity=mockPostEntity();
        UUID randomUUID=UUID.randomUUID();
        HighlightEntity mockHighlight=mockHighlight();

        when(highlightHelper.findHighlightByHighlightId(randomUUID)).thenReturn(mockHighlight);
        doNothing().when(postHelper).isValidateHighlightId(mockMember,randomUUID);
        doNothing().when(postHelper).isValidPostHashTag(mockPostEntity.getHashTag());
        when(postCommandRepository.save(mockPostEntity))
                .thenReturn(PostEntity.builder()
                        .content("content")
                        .hashTag(HashTag.TREE_POINT)
                        .title("title")
                        .postId(111L)
                        .build()
                );

        //when
        Long savedPostId=postManager.save(mockMember,mockPostEntity,randomUUID);

        //then
        assertThat(savedPostId).isEqualTo(111L);
        verify(highlightHelper,times(1)).findHighlightByHighlightId(randomUUID);
        verify(postHelper,times(1)).isValidateHighlightId(mockMember,randomUUID);
        verify(postHelper,times(1)).isValidPostHashTag(mockPostEntity.getHashTag());
    }

    @Test
    @DisplayName("postHelper의 다양한 유효성 검증을 진행하고 게시물을 수정하고 성공 시 postId를 반환합니다.")
    void update(){
        Member mockMember = mockMember();
        PostEntity mockPostEntity = spy(mockPostEntity());
        UUID highlightId = UUID.randomUUID();
        PostRequest postRequest = PostRequest.of(highlightId, "title2", "content2", HashTag.TWO_POINT);
        HighlightEntity mockHighlight = mockHighlight();
        Long postId = 111L;

        when(postQueryRepository.findByPostId(postId)).thenReturn(Optional.of(mockPostEntity));
        doNothing().when(postHelper).isMembersPost(mockPostEntity, mockMember);
        when(highlightHelper.findHighlightByHighlightId(highlightId)).thenReturn(mockHighlight);
        doNothing().when(postHelper).isValidateHighlightId(mockMember, highlightId);
        doNothing().when(postHelper).isValidPostHashTag(postRequest.getHashTag());

        when(postCommandRepository.save(any(PostEntity.class))).thenReturn(mockPostEntity);

        //when
        Long updatedPostId = postManager.update(postRequest, mockMember, postId);

        //then
        assertThat(updatedPostId).isEqualTo(mockPostEntity.getPostId());
        verify(postQueryRepository, times(1)).findByPostId(postId);
        verify(postHelper, times(1)).isMembersPost(mockPostEntity, mockMember);
        verify(postHelper, times(1)).isValidateHighlightId(mockMember, highlightId);
        verify(postHelper, times(1)).isValidPostHashTag(postRequest.getHashTag());
        verify(mockPostEntity, times(1)).update(
                postRequest.getTitle(),
                postRequest.getContent(),
                postRequest.getHashTag(),
                mockHighlight
        );
        verify(postCommandRepository, times(1)).save(mockPostEntity);
    }
    /**
     * mock 하이라이트 영상
     * @return HighlightEntity
     */
    private HighlightEntity mockHighlight() {
        return HighlightEntity.builder()
                .highlightURL("test")
                .highlightKey(UUID.randomUUID())
                .build();
    }

    /**
     * mock 게시 요청 dto 생성
     * @return PostRequest
     */
    private PostRequest mockPostRequest(){
        return PostRequest.of(UUID.randomUUID(),"title","content", HashTag.TREE_POINT);
    }

    /**
     * mock 수정 요청 dto 생성
     * @return PostRequest
     */
    private PostRequest mockUpdatePostRequest(){
        return PostRequest.of(UUID.randomUUID(),"title2","content2", HashTag.TWO_POINT);
    }


    /**
     * mock 게시물 엔티티
     * @return PostEntity
     */
    private PostEntity mockPostEntity(){
        return PostEntity.builder()
                .title("title")
                .content("content")
                .hashTag(HashTag.TREE_POINT)
                .build();
    }

    /**
     * mock Member
     */
    private Member mockMember() {
        return Member.builder()
                .memberId(UUID.randomUUID())
                .email("test@naver.com")
                .username("test")
                .build();
    }
}