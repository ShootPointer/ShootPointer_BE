package com.midas.shootpointer.domain.post.helper.elastic;

import com.midas.shootpointer.domain.post.dto.response.PostSearchHit;
import com.midas.shootpointer.domain.post.dto.response.PostSort;
import com.midas.shootpointer.domain.post.entity.HashTag;
import com.midas.shootpointer.domain.post.entity.PostDocument;
import com.midas.shootpointer.domain.post.entity.PostEntity;
import com.midas.shootpointer.domain.post.mapper.PostElasticSearchMapperImpl;
import com.midas.shootpointer.domain.post.repository.PostElasticSearchRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.elasticsearch.core.SearchHitsImpl;
import org.springframework.data.elasticsearch.core.TotalHitsRelation;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PostElasticSearchUtilImplTest {
    @InjectMocks
    private PostElasticSearchUtilImpl postElasticSearchUtil;

    @Mock
    private PostElasticSearchMapperImpl postElasticSearchMapper;

    @Mock
    private PostElasticSearchRepository postElasticSearchRepository;

    @Test
    @DisplayName("PostDocument 객체를 저장하고 postId를 반환합니다.")
    void createPostDocument() {
        //given
        Long postId=12L;
        LocalDateTime now=LocalDateTime.now();

        PostDocument expected=PostDocument.builder()
                .content("content")
                .createdAt(now)
                .hashTag(HashTag.TWO_POINT.getName())
                .highlightUrl("url")
                .likeCnt(123L)
                .memberName("member")
                .modifiedAt(now)
                .postId(postId)
                .title("title")
                .build();
        PostEntity entity=PostEntity.builder()
                .likeCnt(123L)
                .hashTag(HashTag.TWO_POINT)
                .content("content")
                .postId(postId)
                .title("title")
                .build();

        //when
        when(postElasticSearchMapper.entityToDoc(entity)).thenReturn(expected);
        when(postElasticSearchRepository.save(expected)).thenReturn(expected);

        Long resultPostId=postElasticSearchUtil.createPostDocument(entity);

        //then
        verify(postElasticSearchMapper,times(1)).entityToDoc(entity);
        verify(postElasticSearchRepository,times(1)).save(expected);
        assertThat(resultPostId).isEqualTo(expected.getPostId());
    }

    @Test
    @DisplayName("ElasticSearch로 게시글 검색 시 결과를 PostSearchHit 리스트로 반환합니다.")
    void getPostByTitleOrContentByElasticSearch() {
        //given
        PostDocument doc1=makeDocument(HashTag.TWO_POINT,"title1","content1",1L,123L);
        PostDocument doc2=makeDocument(HashTag.TWO_POINT,"title2","content2",2L,1255L);

        String keyword="title";
        int size=10;
        PostSort sort=new PostSort(1.0f,123123123L,123123123L);

        SearchHit<PostDocument> searchHit1=makeSearchHit ("1L",0.85f,doc1);
        SearchHit<PostDocument> searchHit2=makeSearchHit("2L",0.95f,doc2);

        List<SearchHit<PostDocument>> searchHits=List.of(searchHit1,searchHit2);
        SearchHits<PostDocument> expected= new SearchHitsImpl<>(
                2L,
                TotalHitsRelation.EQUAL_TO,
                0.95f,  // maxScore
                 null,
                 null,
                null,
                searchHits,
                null,
                null,
                null
        );

        //when
        when(postElasticSearchRepository.search(keyword,size,sort)).thenReturn(expected);
        List<PostSearchHit> result=postElasticSearchUtil.getPostByTitleOrContentByElasticSearch(keyword,size,sort);

        //then
        assertThat(result).hasSize(2);
        assertThat(result.get(0).doc().getPostId()).isEqualTo(1L);
        assertThat(result.get(0)._score()).isEqualTo(0.85f);

        assertThat(result.get(1).doc().getPostId()).isEqualTo(2L);
        assertThat(result.get(1)._score()).isEqualTo(0.95f);


    }

    @Test
    @DisplayName("search에 적합한 postDocument 데이터가 없으면 빈 리스트를 반환합니다.")
    void getPostByTitleOrContentByElasticSearch_RETURN_EMPTY_LIST(){
        //given
        String keyword="keyword";
        PostSort sort=new PostSort(213123f,21312312L,123123L);
        int size=10;

        // when
        when(postElasticSearchRepository.search(keyword,size,sort)).thenReturn(null);
        List<PostSearchHit> result=postElasticSearchUtil.getPostByTitleOrContentByElasticSearch(keyword,size,sort);

        //then
        assertThat(result).isEqualTo(Collections.EMPTY_LIST);
    }

    @Test
    @DisplayName("ElasticSearch로 검색어 자동완성을 위한 검색어를 추천합니다-제목 기반")
    void suggestCompleteSearch()  {
        //given
        PostDocument doc1=makeDocument(HashTag.TWO_POINT,"title1","content1",1L,123L);
        PostDocument doc2=makeDocument(HashTag.TWO_POINT,"title2","content2",2L,1255L);

        String keyword="title";

        SearchHit<PostDocument> searchHit1=makeSearchHit ("1L",0.85f,doc1);
        SearchHit<PostDocument> searchHit2=makeSearchHit("2L",0.95f,doc2);

        List<SearchHit<PostDocument>> searchHits=List.of(searchHit1,searchHit2);
        SearchHits<PostDocument> expected= new SearchHitsImpl<>(
                2L,
                TotalHitsRelation.EQUAL_TO,
                0.95f,  // maxScore
                null,
                null,
                null,
                searchHits,
                null,
                null,
                null
        );

        //when
        when(postElasticSearchRepository.suggestCompleteByKeyword(keyword)).thenReturn(expected);
        List<String> result=postElasticSearchUtil.suggestCompleteSearch(keyword);

        //then
        assertThat(result).hasSize(2);
        assertThat(result.get(0)).isEqualTo(doc1.getTitle());
        assertThat(result.get(1)).isEqualTo(doc2.getTitle());

    }

    @Test
    @DisplayName("ElasticSearch로 검색어 자동완성을 위한 검색어가 존재하지 않으면 빈 리스트를 반환합니다.")
    void suggestCompleteSearch_RETURN_EMPTY_LIST() {
        //given
        String keyword="title";

        //when
        when(postElasticSearchRepository.suggestCompleteByKeyword(keyword)).thenReturn(null);
        List<String> result=postElasticSearchUtil.suggestCompleteSearch(keyword);

        //then
        assertThat(result).isEqualTo(Collections.EMPTY_LIST);

    }

    @Test
    @DisplayName("ElasticSearch로 게시글 검색(해시태그) 시 결과를 PostSearchHit 리스트로 반환합니다.(중복 제거 확인)")
    void suggestCompleteSearchWithHashTag(){
        //given
        String tag="2점슛";

        PostDocument doc1=makeDocument(HashTag.TWO_POINT,"title1","content1",1L,123L);
        PostDocument doc2=makeDocument(HashTag.TWO_POINT,"title2","content2",2L,1255L);
        PostDocument doc3=makeDocument(HashTag.TWO_POINT,"title3","content3",3L,5L);


        SearchHit<PostDocument> searchHit1=makeSearchHit ("1L",0.85f,doc1);
        SearchHit<PostDocument> searchHit2=makeSearchHit("2L",0.95f,doc2);
        SearchHit<PostDocument> searchHit3=makeSearchHit("3L",0.75f,doc3);

        List<SearchHit<PostDocument>> searchHits=List.of(searchHit1,searchHit2,searchHit3);
        SearchHits<PostDocument> expected= new SearchHitsImpl<>(
                2L,
                TotalHitsRelation.EQUAL_TO,
                0.95f,  // maxScore
                null,
                null,
                null,
                searchHits,
                null,
                null,
                null
        );

        //when
        when(postElasticSearchRepository.suggestCompleteByHashTag(tag)).thenReturn(expected);
        List<String>result=postElasticSearchUtil.suggestCompleteSearchWithHashTag(tag);

        //then
        assertThat(result).hasSize(1);
        assertThat(result.get(0)).isEqualTo("2점슛");
    }

    @Test
    @DisplayName("ElasticSearch로 게시물의 해시태그로 검색 시 결과를 PostSearchHit 리스트로 반환합니다.")
    void getPostByHashTagByElasticSearch(){
        //given
        String keyword="2점슛";
        int size=10;
        PostSort sort=new PostSort(10f,12123L,123L);

        PostDocument doc1=makeDocument(HashTag.TWO_POINT,"title1","content1",1L,123L);
        PostDocument doc2=makeDocument(HashTag.TWO_POINT,"title2","content2",2L,1255L);
        PostDocument doc3=makeDocument(HashTag.TWO_POINT,"title3","content3",3L,5L);


        SearchHit<PostDocument> searchHit1=makeSearchHit ("1L",0.85f,doc1);
        SearchHit<PostDocument> searchHit2=makeSearchHit("2L",0.95f,doc2);
        SearchHit<PostDocument> searchHit3=makeSearchHit("3L",0.75f,doc3);

        List<SearchHit<PostDocument>> searchHits=List.of(searchHit1,searchHit2,searchHit3);
        SearchHits<PostDocument> expected= new SearchHitsImpl<>(
                2L,
                TotalHitsRelation.EQUAL_TO,
                0.95f,  // maxScore
                null,
                null,
                null,
                searchHits,
                null,
                null,
                null
        );

        //when
        when(postElasticSearchRepository.searchByHashTag(keyword,size,sort)).thenReturn(expected);
        List<PostSearchHit> result=postElasticSearchUtil.getPostByHashTagByElasticSearch(keyword,size,sort);

        //then
        assertThat(result).hasSize(3);
        assertThat(result.get(0)._score()).isEqualTo(0.85f);
        assertThat(result.get(0).doc().getPostId()).isEqualTo(1L);
        assertThat(result.get(0).doc().getHashTag()).isEqualTo("2점슛");

        assertThat(result.get(1)._score()).isEqualTo(0.95f);
        assertThat(result.get(1).doc().getPostId()).isEqualTo(2L);
        assertThat(result.get(1).doc().getHashTag()).isEqualTo("2점슛");

        assertThat(result.get(2)._score()).isEqualTo(0.75f);
        assertThat(result.get(2).doc().getPostId()).isEqualTo(3L);
        assertThat(result.get(2).doc().getHashTag()).isEqualTo("2점슛");
    }

    @Test
    @DisplayName("ElasticSearch로 게시물의 해시태그로 검색 시 결과가 존재하지 않으면 빈 리스트를 반환합니다.")
    void getPostByHashTagByElasticSearch_RETURN_EMPTYLIST(){
        //given
        String keyword="2점슛";
        int size=10;
        PostSort sort=new PostSort(10f,12123L,123L);


        //when
        when(postElasticSearchRepository.searchByHashTag(keyword,size,sort)).thenReturn(null);
        List<PostSearchHit> result=postElasticSearchUtil.getPostByHashTagByElasticSearch(keyword,size,sort);

        //then
        assertThat(result).isEqualTo(Collections.EMPTY_LIST);
    }

    @Test
    @DisplayName("ElasticSearch로 게시글 검색(해시태그) 시 결과가 존재하지 않으면 빈 리스트를 반환합니다.")
    void suggestCompleteSearchWithHashTag_RETURN_EMPTYLIST(){
        //given
        String tag="2점슛";

        //when
        when(postElasticSearchRepository.suggestCompleteByHashTag(tag)).thenReturn(null);
        List<String>result=postElasticSearchUtil.suggestCompleteSearchWithHashTag(tag);

        //then
        assertThat(result).isEqualTo(Collections.EMPTY_LIST);
    }

    @Test
    @DisplayName("#해시태그에서 #를 제거한 String을 반환합니다.")
    void refinedHashTag(){
        //given
        String tag1="#tag1";
        String tag2="# tag212";
        String tag3="ssdfsf#";

        String expectedTag1="tag1";
        String expectedTag2="tag212";
        String expectedTag3="ssdfsf#";

        //when
        String result1=postElasticSearchUtil.refinedHashTag(tag1);
        String result2=postElasticSearchUtil.refinedHashTag(tag2);
        String result3=postElasticSearchUtil.refinedHashTag(tag3);

        //then
        assertThat(result1).isEqualTo(expectedTag1);
        assertThat(result2).isEqualTo(expectedTag2);
        assertThat(result3).isEqualTo(expectedTag3);

    }

    private PostDocument makeDocument(HashTag tag,String title,String content,Long postId,Long likeCnt){
        return PostDocument.builder()
                .highlightUrl("url")
                .content(content)
                .title(title)
                .hashTag(tag.getName())
                .memberName("name")
                .likeCnt(likeCnt)
                .postId(postId)
                .build();
    }

    private SearchHit<PostDocument> makeSearchHit(String id,float _score,PostDocument doc){
        return new SearchHit<>(
                "post",
                id,
                "",
                _score,
                null,
                null,
                null,
                null,
                null,
                null,
                doc
        );
    }
}