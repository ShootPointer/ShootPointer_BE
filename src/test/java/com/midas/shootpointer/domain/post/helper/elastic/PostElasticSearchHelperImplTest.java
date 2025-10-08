package com.midas.shootpointer.domain.post.helper.elastic;

import com.midas.shootpointer.domain.post.dto.response.PostSearchHit;
import com.midas.shootpointer.domain.post.dto.response.PostSort;
import com.midas.shootpointer.domain.post.entity.PostDocument;
import com.midas.shootpointer.domain.post.entity.PostEntity;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@ActiveProfiles({"env","test"})
class PostElasticSearchHelperImplTest {
    @InjectMocks
    private PostElasticSearchHelperImpl postElasticSearchHelper;

    @Mock
    private PostElasticSearchUtil postElasticSearchUtil;

    @Mock
    private PostElasticSearchValidator postElasticSearchValidator;



    @Test
    @DisplayName("PostDocument 객체를 저장합니다 - postElasticSearchUtil.createPostDocument(post) 메서드가 실행되는지 확인합니다.")
    void createPostDocument() {
        //given
        PostEntity post= mock(PostEntity.class);
        Long postDocumentId=12L;

        //when
        when(postElasticSearchUtil.createPostDocument(post)).thenReturn(postDocumentId);
        Long result=postElasticSearchHelper.createPostDocument(post);

        //then
        verify(postElasticSearchUtil,times(1)).createPostDocument(post);
        assertThat(result).isEqualTo(postDocumentId);
    }

    @Test
    @DisplayName("PostDocument 객체를 저장합니다 - postElasticSearchUtil.createPostDocument(post) 메서드가 실행되는지 확인합니다.")
    void getPostByTitleOrContentByElasticSearch() {
        //given
        String search="search";
        int size=10;
        PostSort sort=new PostSort(10f,10L,10L);
        List<PostSearchHit> expected=List.of(new PostSearchHit(makePostDocument(LocalDateTime.now()),10f));

        //when
        when(postElasticSearchUtil.getPostByTitleOrContentByElasticSearch(search,size,sort)).thenReturn(expected);
        List<PostSearchHit> result=postElasticSearchHelper.getPostByTitleOrContentByElasticSearch(search,size,sort);

        //then
        verify(postElasticSearchUtil,times(1)).getPostByTitleOrContentByElasticSearch(search,size,sort);
        assertThat(result.get(0)._score()).isEqualTo(expected.get(0)._score());
        assertThat(result.get(0).doc().getPostId()).isEqualTo(expected.get(0).doc().getPostId());

    }

    @Test
    @DisplayName("자동 완성 검색어를 조회합니다 - postElasticSearchUtil.suggestCompleteSearch(keyword) 메서드가 실행되는지 확인합니다.")
    void suggestCompleteSearch() {
        //given
        String keyword=anyString();
        List<String> expected=List.of("테스트","테스트1","테스트2","테스트3");

        //when
        when(postElasticSearchUtil.suggestCompleteSearch(keyword)).thenReturn(expected);
        List<String> result=postElasticSearchHelper.suggestCompleteSearch(keyword);

        //then
        verify(postElasticSearchUtil,times(1)).suggestCompleteSearch(keyword);
        assertThat(result.size()).isEqualTo(expected.size());

        for (int idx=0;idx<result.size();idx++){
            assertThat(result.get(idx)).isEqualTo(expected.get(idx));
        }
    }

    @Test
    @DisplayName("게시물 검색(해시태그 기반)을 이용하여 게시물을 조회합니다 - postElasticSearchUtil.getPostByHashTagByElasticSearch(search,size,sort) 메서드가 실행되는지 확인합니다.")
    void getPostByHashTagByElasticSearch() {
        //given
        String search="search";
        int size=10;
        PostSort sort=new PostSort(10f,10L,10L);
        PostDocument mockDocument=makePostDocument(LocalDateTime.now());

        List<PostSearchHit> searchHits=List.of(new PostSearchHit(mockDocument,10f));

        //when
        when(postElasticSearchUtil.getPostByHashTagByElasticSearch(search,size,sort)).thenReturn(searchHits);
        List<PostSearchHit> result=postElasticSearchHelper.getPostByHashTagByElasticSearch(search,size,sort);

        //then
        verify(postElasticSearchUtil,times(1)).getPostByHashTagByElasticSearch(search,size,sort);

        assertThat(result).hasSize(1);
        assertThat(result.get(0)._score()).isEqualTo(sort._score());
        assertThat(result.get(0).doc().getPostId()).isEqualTo(mockDocument.getPostId());
    }

    @Test
    @DisplayName("해시태그를 기반으로 한 해시태그 자동완성 조회를 합니다 - postElasticSearchUtil.suggestCompleteSearchWithHashTag(tag) 메서드가 실행되는지 확인합니다.")
    void suggestCompleteSearchWithHashTag() {
        //given
        String tag="#tag";
        List<String> expected=List.of("string1","string2");

        //when
        when(postElasticSearchUtil.suggestCompleteSearchWithHashTag(tag)).thenReturn(expected);
        List<String> result=postElasticSearchHelper.suggestCompleteSearchWithHashTag(tag);

        //then
        verify(postElasticSearchUtil,times(1)).suggestCompleteSearchWithHashTag(tag);
        for (int i=0;i<result.size();i++){
            assertThat(result.get(i)).isEqualTo(expected.get(i));
        }
    }

    @Test
    @DisplayName("키워드가 해시태그이면 #를 제거합니다 - postElasticSearchUtil.refinedHashTag(hashTag) 메서드가 실행되는지 확인합니다.")
    void refinedHashTag() {
        //given
        String hashTag="#tag";
        String expected="tag";

        //when
        when(postElasticSearchUtil.refinedHashTag(hashTag)).thenReturn(expected);
        String result=postElasticSearchHelper.refinedHashTag(hashTag);

        //then
        verify(postElasticSearchUtil,times(1)).refinedHashTag(hashTag);
        assertThat(result).isEqualTo(expected);

    }

    @Test
    @DisplayName("키워드 유형이 해시태그인지 확인합니다 - postElasticSearchValidator.isHashTagSearch(keyword) 메서드가 실행되는지 확인합니다.")
    void isHashTagSearch() {
        //given
        String keyword="keyword";
        boolean bool=true;

        //when
        when(postElasticSearchValidator.isHashTagSearch(keyword)).thenReturn(bool);
        postElasticSearchHelper.isHashTagSearch(keyword);

        //then
        verify(postElasticSearchValidator,times(1)).isHashTagSearch(keyword);
    }

    private PostDocument makePostDocument(LocalDateTime localDateTime){
        return PostDocument.builder()
                .content("content")
                .createdAt(localDateTime)
                .hashTag("3점슛")
                .likeCnt(100L)
                .highlightUrl("url")
                .memberName("name")
                .modifiedAt(localDateTime)
                .postId(1L)
                .title("title")
                .build();
    }
}