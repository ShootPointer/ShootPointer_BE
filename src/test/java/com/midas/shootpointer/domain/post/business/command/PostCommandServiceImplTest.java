package com.midas.shootpointer.domain.post.business.command;

import com.midas.shootpointer.domain.highlight.entity.HighlightEntity;
import com.midas.shootpointer.domain.member.entity.Member;
import com.midas.shootpointer.domain.post.business.PostManager;
import com.midas.shootpointer.domain.post.dto.request.PostRequest;
import com.midas.shootpointer.domain.post.entity.HashTag;
import com.midas.shootpointer.domain.post.entity.PostEntity;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PostCommandServiceImplTest {
    @InjectMocks
    private PostCommandServiceImpl postCommandService;

    @Mock
    private PostManager postManager;

    @Test
    @DisplayName("게시물 저장 시 PostDto->PostEntity 호출 여부를 확인하고 postManager-save 호출 여부를 확인합니다.")
    void create(){
        //given
        Member member=mockMember();
        HighlightEntity highlight=mockHighlightEntity(member);
        PostEntity postEntity=mockPostEntity("123",highlight);
        UUID highlightId=postEntity.getHighlight().getHighlightId();

        //when
        when(postManager.save(member,postEntity,highlightId)).thenReturn(111L);

        //then
        Long createdPostId=postCommandService.create(postEntity,member);
        verify(postManager,times(1)).save(member,postEntity,highlightId);
        assertThat(createdPostId).isEqualTo(111L);
    }

    @Test
    @DisplayName("게시물 수정 시 postManager-update 호출 여부를 확인합니다.")
    void update(){
        //given
        Long postId=111L;
        Member member=mockMember();
        HighlightEntity highlightEntity=mockHighlightEntity(member);
        PostEntity newPost=mockPostEntity("new",highlightEntity);

        //when
        when(postManager.update(newPost,member,postId)).thenReturn(111L);

        //then
        Long createdPostId=postCommandService.update(newPost,member,postId);
        verify(postManager,times(1)).update(newPost,member,postId);
        assertThat(createdPostId).isEqualTo(111L);
    }

    @Test
    @DisplayName("게시물 삭제 시 postManager-delete 호출 여부를 확인합니다.")
    void delete(){
        //given
        Member mockMember=mockMember();
        Long postId=111L;

        //when
        when(postManager.delete(postId,mockMember)).thenReturn(111L);

        //then
        Long deletedPostId=postCommandService.delete(mockMember,postId);
        verify(postManager,times(1)).delete(postId,mockMember);
        assertThat(deletedPostId).isEqualTo(111L);
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
    private PostEntity mockPostEntity(String str,HighlightEntity highlight){
        return PostEntity.builder()
                .title("title"+str)
                .content("content"+str)
                .hashTag(HashTag.TREE_POINT)
                .highlight(highlight)
                .build();
    }

    /**
     * mock highlight
     */
    private HighlightEntity mockHighlightEntity(Member member){
        return HighlightEntity.builder()
                .highlightId(UUID.randomUUID())
                .highlightURL("testetst")
                .member(member)
                .highlightKey(UUID.randomUUID())
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