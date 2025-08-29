package com.midas.shootpointer.domain.post.business;

import com.midas.shootpointer.domain.member.entity.Member;
import com.midas.shootpointer.domain.post.business.command.PostCommandService;
import com.midas.shootpointer.domain.post.business.command.PostCommandServiceImpl;
import com.midas.shootpointer.domain.post.dto.PostRequest;
import com.midas.shootpointer.domain.post.entity.HashTag;
import com.midas.shootpointer.domain.post.entity.PostEntity;
import com.midas.shootpointer.domain.post.helper.PostHelper;
import com.midas.shootpointer.domain.post.mapper.PostMapper;
import com.midas.shootpointer.domain.post.repository.PostCommandRepository;
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
    private PostCommandRepository postCommandRepository;

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
        verify(postHelper,times(1)).isValidateHighlightId(mockMember,randomUUID);
        verify(postHelper,times(1)).isValidPostHashTag(mockPostEntity.getHashTag());
    }

    /**
     * mock 게시 요청 dto 생성
     * @return PostRequest
     */
    private PostRequest mockPostRequest(){
        return PostRequest.of(UUID.randomUUID(),"title","content", HashTag.TREE_POINT);
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