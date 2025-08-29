package com.midas.shootpointer.domain.post.business.command;

import com.midas.shootpointer.domain.member.entity.Member;
import com.midas.shootpointer.domain.post.business.PostManager;
import com.midas.shootpointer.domain.post.dto.PostRequest;
import com.midas.shootpointer.domain.post.entity.HashTag;
import com.midas.shootpointer.domain.post.entity.PostEntity;
import com.midas.shootpointer.domain.post.mapper.PostMapper;
import org.assertj.core.api.Assertions;
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
class PostCommandServiceImplTest {
    @InjectMocks
    private PostCommandServiceImpl postCommandService;

    @Mock
    private PostManager postManager;

    @Mock
    private PostMapper postMapper;

    @Test
    @DisplayName("게시물 저장 시 PostDto->PostEntity 호출 여부를 확인하고 postManager-save 호출 여부를 확인합니다.")
    void create(){
        //given
        PostRequest request=mockPostRequest();
        PostEntity postEntity=mockPostEntity();
        Member member=mockMember();

        //when
        when(postMapper.dtoToEntity(request,member)).thenReturn(postEntity);
        when(postManager.save(member,postEntity,request.getHighlightId())).thenReturn(111L);

        //then
        Long createdPostId=postCommandService.create(request,member);
        verify(postMapper,times(1)).dtoToEntity(request,member);
        verify(postManager,times(1)).save(member,postEntity,request.getHighlightId());
        assertThat(createdPostId).isEqualTo(111L);
    }

    /**
     * mock PostRequest
     */
    private PostRequest mockPostRequest(){
        return PostRequest.of(UUID.randomUUID(),"title","content", HashTag.TREE_POINT);
    }
    /**
     * mock postEntity
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