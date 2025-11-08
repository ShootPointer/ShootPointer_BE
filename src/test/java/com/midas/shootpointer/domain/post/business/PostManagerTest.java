package com.midas.shootpointer.domain.post.business;

import com.midas.shootpointer.domain.highlight.entity.HighlightEntity;
import com.midas.shootpointer.domain.highlight.helper.HighlightHelper;
import com.midas.shootpointer.domain.member.entity.Member;
import com.midas.shootpointer.domain.post.dto.response.*;
import com.midas.shootpointer.domain.post.entity.HashTag;
import com.midas.shootpointer.domain.post.entity.PostDocument;
import com.midas.shootpointer.domain.post.entity.PostEntity;
import com.midas.shootpointer.domain.post.helper.elastic.PostElasticSearchHelper;
import com.midas.shootpointer.domain.post.helper.simple.PostHelper;
import com.midas.shootpointer.domain.post.mapper.PostMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@ActiveProfiles({"dev", "es"})
class PostManagerTest {
    @InjectMocks
    private PostManager postManager;

    @Mock
    private PostHelper postHelper;

    @Mock
    private HighlightHelper highlightHelper;

    @Mock
    private PostMapper postMapper;

    @Mock
    private PostElasticSearchHelper elasticSearchHelper;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(postManager, "postElasticSearchHelper", elasticSearchHelper);
    }

    @Test
    @DisplayName(
            "Highlight URL이 유저의 영상으로 일치 여부를 검증하고 " +
            "해시태그가 올바른 지 여부를 확인하고 게시물을 저장하고 " +
            "반환값으로 게시물의 id를 반환합니다."
    )
    void save() {
        //given
        Member mockMember = mockMember();
        UUID randomUUID = UUID.randomUUID();
        HighlightEntity mockHighlight = mockHighlight(randomUUID);
        PostEntity mockPostEntity = mockPostEntity("", mockMember);

        //저장된 게시물
        PostEntity savedPostEntity = createAndSavedPostEntity(mockHighlight, mockMember, 111L, "");

        when(highlightHelper.findHighlightByHighlightId(randomUUID)).thenReturn(mockHighlight);
        doNothing().when(postHelper).isValidateHighlightId(mockMember, randomUUID);
        doNothing().when(postHelper).isValidPostHashTag(mockPostEntity.getHashTag());
        when(postHelper.save(any(PostEntity.class))).thenReturn(savedPostEntity);


        //when
        Long savedPostId = postManager.save(mockMember, mockPostEntity, randomUUID);

        //then
        assertThat(savedPostId).isEqualTo(111L);
        assertThat(mockPostEntity.getHighlight()).isEqualTo(mockHighlight);

        verify(highlightHelper, times(1)).findHighlightByHighlightId(randomUUID);
        verify(postHelper, times(1)).isValidateHighlightId(mockMember, randomUUID);
        verify(postHelper, times(1)).isValidPostHashTag(mockPostEntity.getHashTag());
    }


    @Test
    @DisplayName("postHelper의 다양한 유효성 검증을 진행하고 게시물을 수정하고 성공 시 postId를 반환합니다.")
    void update() {
        Member mockMember = mockMember();
        UUID highlightId = UUID.randomUUID();
        HighlightEntity mockHighlight = mockHighlight(highlightId);
        PostEntity newPost = spy(PostEntity.builder()
                .content("content2")
                .hashTag(HashTag.TWO_POINT)
                .highlight(mockHighlight)
                .member(mockMember)
                .title("title2")
                .build());
        Long postId = 111L;
        PostEntity existedPost = createAndSavedPostEntity(mockHighlight, mockMember, postId, "exist");

        when(postHelper.findPostByPostId(postId)).thenReturn(existedPost);

        doNothing().when(postHelper).isMembersPost(existedPost, mockMember);

        when(highlightHelper.findHighlightByHighlightId(highlightId)).thenReturn(mockHighlight);
        doNothing().when(postHelper).isValidateHighlightId(mockMember, highlightId);
        doNothing().when(postHelper).isValidPostHashTag(any(HashTag.class));

        when(postHelper.update(newPost, existedPost, mockHighlight))
                .thenReturn(newPost);

        //when
        Long updatedPostId = postManager.update(newPost, mockMember, postId);

        //then
        assertThat(updatedPostId).isEqualTo(newPost.getPostId());
        verify(postHelper, times(1)).findPostByPostId(postId);
        verify(postHelper, times(1)).isMembersPost(existedPost, mockMember);
        verify(postHelper, times(1)).isValidateHighlightId(mockMember, highlightId);
        verify(postHelper, times(1)).isValidPostHashTag(newPost.getHashTag());
        verify(postHelper, times(1)).update(newPost, existedPost, mockHighlight);
    }

    @Test
    @DisplayName("postHelper의 다양한 유효성 검증을 진행하고 게시물을 삭제하고 성공 시 postId를 반환합니다.")
    void delete() {
        Member mockMember = mockMember();
        PostEntity mockPostEntity = spy(mockPostEntity("", mockMember));
        Long postId = 111L;

        when(postHelper.findPostByPostId(postId)).thenReturn(mockPostEntity);
        doNothing().when(postHelper).isMembersPost(mockPostEntity, mockMember);

        //when
        Long deletedPostId = postManager.delete(postId, mockMember);

        //then
        assertThat(deletedPostId).isEqualTo(mockPostEntity.getPostId());
        verify(postHelper, times(1)).findPostByPostId(postId);
        verify(postHelper, times(1)).isMembersPost(mockPostEntity, mockMember);
        verify(mockPostEntity, times(1)).delete();

    }

    @Test
    @DisplayName("type 값이 올바른지 확인하고 type 값에 맞는 postHelper의 메소드들을 호출합니다._POPULAR")
    void multiRead_POPULAR() {
        //given
        String type = "popular";
        Long lastPostId = 12L;
        int size = 10;
        List<PostEntity> postEntities = new ArrayList<>();
        List<PostResponse> responses = new ArrayList<>();
        PostListResponse postListResponse = PostListResponse.of(lastPostId, responses);


        //when
        when(postHelper.isValidAndGetPostOrderType(type)).thenReturn(PostOrderType.popular);
        when(postHelper.getPopularPostListBySliceAndNoOffset(lastPostId, size)).thenReturn(postEntities);
        when(postMapper.entityToDto(postEntities)).thenReturn(postListResponse);

        postManager.multiRead(lastPostId, type, size);

        //then
        verify(postHelper, times(1)).getPopularPostListBySliceAndNoOffset(lastPostId, size);
        verify(postMapper, times(1)).entityToDto(postEntities);
    }

    @Test
    @DisplayName("type 값이 올바른지 확인하고 type 값에 맞는 postHelper의 메소드들을 호출합니다._LATEST")
    void multiRead_LATEST() {
        //given
        String type = "latest";
        Long lastPostId = 12L;
        int size = 10;
        List<PostEntity> postEntities = new ArrayList<>();
        List<PostResponse> responses = new ArrayList<>();
        PostListResponse postListResponse = PostListResponse.of(lastPostId, responses);


        //when
        when(postHelper.isValidAndGetPostOrderType(type)).thenReturn(PostOrderType.latest);
        when(postHelper.getLatestPostListBySliceAndNoOffset(lastPostId, size)).thenReturn(postEntities);
        when(postMapper.entityToDto(postEntities)).thenReturn(postListResponse);

        postManager.multiRead(lastPostId, type, size);

        //then
        verify(postHelper, times(1)).getLatestPostListBySliceAndNoOffset(lastPostId, size);
        verify(postMapper, times(1)).entityToDto(postEntities);
    }

    @Test
    @DisplayName("postId로 게시물을 단 건 조회하며 성공 시 PostResponse를 반환합니다.")
    void singleRead() {
        //given
        Long postId = 123L;
        Member member = mockMember();
        PostEntity expectedPost = mockPostEntity("", member);
        PostResponse expectedResponse = PostResponse.builder()
                .content(expectedPost.getContent())
                .hashTag(expectedPost.getHashTag().getName())
                .likeCnt(expectedPost.getLikeCnt())
                .createdAt(LocalDateTime.now())
                .highlightUrl("url")
                .memberName(expectedPost.getMember().getUsername())
                .modifiedAt(LocalDateTime.now())
                .title(expectedPost.getTitle())
                .build();

        //when
        when(postHelper.findPostByPostId(postId)).thenReturn(expectedPost);
        when(postMapper.entityToDto(expectedPost)).thenReturn(expectedResponse);
        postManager.singleRead(postId);

        //then
        verify(postHelper, times(1)).findPostByPostId(postId);
        verify(postMapper, times(1)).entityToDto(expectedPost);
    }

    @Test
    @DisplayName("search : String을 입력받아 조건에 맞는 게시물을 조회하는 postHelper 메서드를 호출합니다.")
    void getPostEntitiesByPostTitleOrPostContent() {
        //given
        Long postId = 12415315L;
        String search = "test";
        int size = 10;

        List<PostEntity> postEntityList = new ArrayList<>();
        List<PostResponse> postResponses = new ArrayList<>();
        Member member = mockMember();
        for (int i = 0; i < 2; i++) {
            postEntityList.add(mockPostEntity("", member));
            postResponses.add(PostResponse.builder()
                    .title(postEntityList.get(i).getTitle())
                    .memberName(member.getUsername())
                    .content(postEntityList.get(i).getContent())
                    .createdAt(LocalDateTime.now())
                    .modifiedAt(LocalDateTime.now())
                    .highlightUrl("test")
                    .likeCnt(100L)
                    .hashTag(HashTag.THREE_POINT.getName())
                    .build());
        }
        PostListResponse postListResponse = PostListResponse.of(postId, postResponses);

        //when
        when(postHelper.getPostEntitiesByPostTitleOrPostContent(search, postId, size))
                .thenReturn(postEntityList);
        when(postMapper.entityToDto(postEntityList)).thenReturn(postListResponse);

        postManager.getPostEntitiesByPostTitleOrPostContent(search, postId, size);

        //then
        verify(postHelper, times(1)).getPostEntitiesByPostTitleOrPostContent(search, postId, size);
        verify(postMapper, times(1)).entityToDto(postEntityList);
    }

    @DisplayName("제목과 내용을 기반으로 한 ElasticSearch로 게시물을 검색합니다.")
    @Test
    void getPostByPostTitleOrPostContentByElasticSearch() {
        //given
        int size = 10;
        String search = "title";
        PostSort sort = new PostSort(0.7f, 1234567L, 1234567L);

        PostDocument doc1 = makeDocument(HashTag.TWO_POINT, "title1", "content1", 12L, 12312L);
        PostDocument doc2 = makeDocument(HashTag.TWO_POINT, "title2", "content2", 20L, 12314122L);
        PostDocument doc3 = makeDocument(HashTag.TWO_POINT, "title3", "content3", 142L, 12322212L);

        PostResponse response1 = makePostResponse(doc1);
        PostResponse response2 = makePostResponse(doc2);
        PostResponse response3 = makePostResponse(doc3);

        List<PostSearchHit> responses = List.of(
                new PostSearchHit(doc1, 0.5f),
                new PostSearchHit(doc2, 0.6f),
                new PostSearchHit(doc3, 0.7f)
        );

        //when
        doNothing().when(postHelper).isValidSize(size);
        when(postHelper.isValidInput(search)).thenReturn(true);
        when(elasticSearchHelper.isHashTagSearch(search)).thenReturn(false);
        when(elasticSearchHelper.getPostByTitleOrContentByElasticSearch(search, size, sort))
                .thenReturn(responses);
        when(postMapper.documentToResponse(doc1)).thenReturn(response1);
        when(postMapper.documentToResponse(doc2)).thenReturn(response2);
        when(postMapper.documentToResponse(doc3)).thenReturn(response3);

        PostListResponse result = postManager.getPostByPostTitleOrPostContentByElasticSearch(search, size, sort);

        //then
        assertThat(result.getPostList()).hasSize(3);
        assertThat(result.getLastPostId()).isEqualTo(142L);
        assertThat(result.getSort().likeCnt()).isEqualTo(12322212L);
        assertThat(result.getSort().lastPostId()).isEqualTo(142L);
        assertThat(result.getSort()._score()).isEqualTo(0.7f);

        assertThat(result.getPostList().get(0).getPostId()).isEqualTo(12L);
        assertThat(result.getPostList().get(1).getPostId()).isEqualTo(20L);
        assertThat(result.getPostList().get(2).getPostId()).isEqualTo(142L);

        // 검증
        verify(postHelper).isValidSize(size);
        verify(postHelper).isValidInput(search);
        verify(elasticSearchHelper).isHashTagSearch(search);
        verify(elasticSearchHelper).getPostByTitleOrContentByElasticSearch(search, size, sort);
        verify(postMapper, times(3)).documentToResponse(any(PostDocument.class));
    }

    @DisplayName("ElasticSearchHelper가 주입되지 않으면 일반 SQL 쿼리로 검색을 실행합니다.")
    @Test
    void getPostByPostTitleOrPostContentByElasticSearch_POSTELASTICSEARCH_NULL(){
        //given
        String search="keyword";
        int size=10;
        PostSort sort=new PostSort(0.7f,1213L,123124L);
        // elasticSearchHelper를 null로 설정
        ReflectionTestUtils.setField(postManager, "postElasticSearchHelper", null);

        List<PostEntity> entities = new ArrayList<>();
        PostListResponse expectedResponse = PostListResponse.of(123124L, new ArrayList<>());

        when(postHelper.getPostEntitiesByPostTitleOrPostContent(search,123124L,size)).thenReturn(entities);
        when(postMapper.entityToDto(entities)).thenReturn(expectedResponse);

        //when
        postManager.getPostByPostTitleOrPostContentByElasticSearch(search,size,sort);

        //then
        verify(postHelper,times(1)).getPostEntitiesByPostTitleOrPostContent(search,123124L,size);
        verify(postMapper,times(1)).entityToDto(entities);
        verifyNoInteractions(elasticSearchHelper);
    }

    @DisplayName("제목과 내용을 기반으로 한 ElasticSearch로 게시물 검색 시 검색어가 빈 문자열이면 빈 리스트를 반환합니다.")
    @Test
    void getPostByPostTitleOrPostContentByElasticSearch_RETURN_EMPTYLIST() {
        //given
        String search = "search";
        int size = 10;
        PostSort sort = new PostSort(0.5f, 1000L, 10000L);

        //when
        doNothing().when(postHelper).isValidSize(size);
        when(postHelper.isValidInput(search)).thenReturn(false);

        //then
        PostListResponse result = postManager.getPostByPostTitleOrPostContentByElasticSearch(search, size, sort);
        assertThat(result.getPostList()).isEqualTo(Collections.EMPTY_LIST);
        assertThat(result.getSort()).isEqualTo(sort);
        assertThat(result.getLastPostId()).isEqualTo(sort.lastPostId());
    }

    @DisplayName("제목과 내용을 기반으로 한 ElasticSearch로 게시물 검색 시 해시태그 형식이면 postElasticSearchHelper의 getPostByHashTagByElasticSearch을 호출합니다,")
    @Test
    void getPostByPostTitleOrPostContentByElasticSearch_BY_HASHTAG() {
        //given
        String search = "#해시태그";
        String refinedSearch = "해시태그";
        int size = 10;
        PostSort sort = new PostSort(0.7f, 1234567L, 1234567L);

        PostDocument doc1 = makeDocument(HashTag.TWO_POINT, "title1", "content1", 12L, 12312L);
        PostDocument doc2 = makeDocument(HashTag.TWO_POINT, "title2", "content2", 20L, 12314122L);

        PostResponse response1 = makePostResponse(doc1);
        PostResponse response2 = makePostResponse(doc2);

        List<PostSearchHit> responses = List.of(
                new PostSearchHit(doc1, 0.5f),
                new PostSearchHit(doc2, 0.6f)
        );

        //when
        doNothing().when(postHelper).isValidSize(size);
        when(postHelper.isValidInput(search)).thenReturn(true);
        when(elasticSearchHelper.isHashTagSearch(search)).thenReturn(true);
        when(elasticSearchHelper.refinedHashTag(search)).thenReturn(refinedSearch);
        when(elasticSearchHelper.getPostByHashTagByElasticSearch(refinedSearch, size, sort))
                .thenReturn(responses);
        when(postMapper.documentToResponse(doc1)).thenReturn(response1);
        when(postMapper.documentToResponse(doc2)).thenReturn(response2);

        PostListResponse result = postManager.getPostByPostTitleOrPostContentByElasticSearch(search, size, sort);

        //then
        assertThat(result).isNotNull();
        assertThat(result.getPostList()).hasSize(2);
        assertThat(result.getLastPostId()).isEqualTo(20L);

        verify(postHelper).isValidSize(size);
        verify(postHelper).isValidInput(search);
        verify(elasticSearchHelper).isHashTagSearch(search);
        verify(elasticSearchHelper).refinedHashTag(search);
        verify(elasticSearchHelper).getPostByHashTagByElasticSearch(refinedSearch, size, sort);
        verify(postMapper, times(2)).documentToResponse(any(PostDocument.class));
    }

    @DisplayName("제목을 기반으로 검색어 자동완성 리스트를 조회합니다,")
    @Test
    void searchAutoCompleteResponse() {
        //given
        String keyword = "keyword";
        List<String> expected = List.of(
                "keyword1",
                "keyword2",
                "keyword3",
                "keyword4"
        );

        //when
        when(postHelper.isValidInput(keyword)).thenReturn(true);
        when(elasticSearchHelper.isHashTagSearch(keyword)).thenReturn(false);
        when(elasticSearchHelper.suggestCompleteSearch(keyword)).thenReturn(expected);

        List<SearchAutoCompleteResponse> result = postManager.suggest(keyword);

        //then
        assertThat(result).isNotEmpty();
        assertThat(result.get(0).getSuggest()).isEqualTo("keyword1");
        assertThat(result.get(1).getSuggest()).isEqualTo("keyword2");
        assertThat(result.get(2).getSuggest()).isEqualTo("keyword3");
        assertThat(result.get(3).getSuggest()).isEqualTo("keyword4");

        verify(postHelper).isValidInput(keyword);
        verify(elasticSearchHelper).isHashTagSearch(keyword);
        verify(elasticSearchHelper).suggestCompleteSearch(keyword);
    }

    @DisplayName("ElasticSearchHelper가 주입되지 않으면 빈 리스트를 반환합니다.")
    @Test
    void searchAutoCompleteResponse_POSTELASTICSEARCH_NULL(){
        //given
        // elasticSearchHelper를 null로 설정
        ReflectionTestUtils.setField(postManager, "postElasticSearchHelper", null);
        String keyword="keyword";

        //when
        List<SearchAutoCompleteResponse> result=postManager.suggest(keyword);

        //then
        assertThat(result).isEqualTo(Collections.EMPTY_LIST);
    }

    @Test
    @DisplayName("제목을 기반으로 검색어 자동 완성 시 빈 문자열이면 빈 리스트를 반환합니다.")
    void searchAutoCompleteResponse_RETURN_EMPTYLIST() {
        //given
        String keyword = "keyword";

        //when
        when(postHelper.isValidInput(keyword)).thenReturn(false);
        List<SearchAutoCompleteResponse> result = postManager.suggest(keyword);

        //then
        assertThat(result).isEqualTo(Collections.EMPTY_LIST);
    }

    @Test
    @DisplayName("제목을 기반으로 검색어 자동 완성 시 해시태그 형식이면 postElasticSearchHelper.suggestCompleteSearchWithHashTag을 호출합니다.")
    void searchAutoCompleteResponse_BY_HASHTAG() {
        //given
        String keyword = "#keyword";
        String refinedKeyword = "keyword";
        List<String> expected = List.of("keyword1", "keyword2", "keyword3");

        //when
        when(postHelper.isValidInput(keyword)).thenReturn(true);
        when(elasticSearchHelper.isHashTagSearch(keyword)).thenReturn(true);
        when(elasticSearchHelper.refinedHashTag(keyword)).thenReturn(refinedKeyword);
        when(elasticSearchHelper.suggestCompleteSearchWithHashTag(refinedKeyword)).thenReturn(expected);

        List<SearchAutoCompleteResponse> result = postManager.suggest(keyword);

        //then
        assertThat(result).isNotEmpty();
        assertThat(result).hasSize(3);
        assertThat(result.get(0).getSuggest()).isEqualTo("keyword1");
        assertThat(result.get(1).getSuggest()).isEqualTo("keyword2");
        assertThat(result.get(2).getSuggest()).isEqualTo("keyword3");

        // 검증
        verify(postHelper).isValidInput(keyword);
        verify(elasticSearchHelper).isHashTagSearch(keyword);
        verify(elasticSearchHelper).refinedHashTag(keyword);
        verify(elasticSearchHelper).suggestCompleteSearchWithHashTag(refinedKeyword);
    }
    
    @Test
    @DisplayName("회원 ID로 해당 회원의 게시물 목록을 조회합니다._SUCCESS")
    void getMyPosts_SUCCESS() {
        //given
        UUID memberId = UUID.randomUUID();
        Member member = mockMember();
        HighlightEntity highlight = mockHighlight(UUID.randomUUID());
        
        List<Long> postIds = List.of(1L, 2L, 3L);
        List<PostEntity> postEntities = List.of(
            createAndSavedPostEntity(highlight, member, 1L, "1"),
            createAndSavedPostEntity(highlight, member, 2L, "2"),
            createAndSavedPostEntity(highlight, member, 3L, "3")
        );
        
        PostResponse response1 = makePostResponse(
            LocalDateTime.now(), 1L, 10L, "title1", "content1"
        );
        PostResponse response2 = makePostResponse(
            LocalDateTime.now(), 2L, 20L, "title2", "content2"
        );
        PostResponse response3 = makePostResponse(
            LocalDateTime.now(), 3L, 30L, "title3", "content3"
        );
        
        //when
        when(postHelper.findPostIdsByMemberId(memberId)).thenReturn(postIds);
        when(postHelper.findPostsByPostIds(postIds)).thenReturn(postEntities);
        when(postMapper.entityToDto(postEntities.get(0))).thenReturn(response1);
        when(postMapper.entityToDto(postEntities.get(1))).thenReturn(response2);
        when(postMapper.entityToDto(postEntities.get(2))).thenReturn(response3);
        
        PostListResponse result = postManager.getMyPosts(memberId);
        
        //then
        assertThat(result).isNotNull();
        assertThat(result.getPostList()).hasSize(3);
        assertThat(result.getLastPostId()).isEqualTo(3L);
        assertThat(result.getPostList().get(0).getPostId()).isEqualTo(1L);
        assertThat(result.getPostList().get(1).getPostId()).isEqualTo(2L);
        assertThat(result.getPostList().get(2).getPostId()).isEqualTo(3L);
        
        verify(postHelper, times(1)).findPostIdsByMemberId(memberId);
        verify(postHelper, times(1)).findPostsByPostIds(postIds);
        verify(postMapper, times(3)).entityToDto(any(PostEntity.class));
    }
    
    @Test
    @DisplayName("회원의 게시물이 없으면 빈 리스트를 반환합니다._EMPTY")
    void getMyPosts_EMPTY() {
        //given
        UUID memberId = UUID.randomUUID();
        List<Long> emptyPostIds = List.of();
        
        //when
        when(postHelper.findPostIdsByMemberId(memberId)).thenReturn(emptyPostIds);
        
        PostListResponse result = postManager.getMyPosts(memberId);
        
        //then
        assertThat(result).isNotNull();
        assertThat(result.getPostList()).isEmpty();
        assertThat(result.getLastPostId()).isNull();
        
        verify(postHelper, times(1)).findPostIdsByMemberId(memberId);
        verify(postHelper, never()).findPostsByPostIds(any());
        verify(postMapper, never()).entityToDto(any(PostEntity.class));
    }
    
    // PostResponse 생성 헬퍼 메서드 추가
    private PostResponse makePostResponse(
        LocalDateTime time,
        Long postId,
        Long likeCnt,
        String title,
        String content
    ) {
        return PostResponse.builder()
            .content(content)
            .likeCnt(likeCnt)
            .createdAt(time)
            .modifiedAt(time)
            .highlightUrl("test")
            .postId(postId)
            .title(title)
            .hashTag(HashTag.TWO_POINT.getName())
            .memberName("testUser")
            .build();
    }
    
    private PostResponse makePostResponse(PostDocument doc) {
        return PostResponse.builder()
                .content(doc.getContent())
                .likeCnt(doc.getLikeCnt())
                .highlightUrl(doc.getHighlightUrl())
                .modifiedAt(doc.getModifiedAt())
                .memberName(doc.getMemberName())
                .createdAt(doc.getCreatedAt())
                .hashTag(doc.getHashTag())
                .postId(doc.getPostId())
                .title(doc.getTitle())
                .build();
    }

    /**
     * mock 하이라이트 영상
     *
     * @return HighlightEntity
     */
    private HighlightEntity mockHighlight(UUID highlightId) {
        return HighlightEntity.builder()
                .highlightId(highlightId)
                .highlightURL("test")
                .highlightKey(UUID.randomUUID())
                .build();
    }


    /**
     * mock 게시물 엔티티
     *
     * @return PostEntity
     */
    private PostEntity mockPostEntity(String str, Member member) {
        return PostEntity.builder()
                .title("title" + str)
                .content("content" + str)
                .member(member)
                .hashTag(HashTag.THREE_POINT)
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

    private PostEntity createAndSavedPostEntity(HighlightEntity highlight, Member member, Long postId, String str) {
        return PostEntity.builder()
                .member(member)
                .postId(postId)
                .highlight(highlight)
                .hashTag(HashTag.THREE_POINT)
                .content("content" + str)
                .title("title" + str)
                .build();
    }

    private PostDocument makeDocument(HashTag tag, String title, String content, Long postId, Long likeCnt) {
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

    @Nested
    @DisplayName("PostListResponseFactory 테스트")
    class PostListResponseFactoryTest {
        @InjectMocks
        private com.midas.shootpointer.domain.post.business.PostManager postManager;

        @Mock
        private PostMapper mapper;


        @DisplayName("PostListResponse 형태로 변환합니다.")
        @Test
        void build_PostSearchHit_To_PostListResponse() {
            //given
            PostDocument doc1 = makeDocument(20L, "title1", "content1", 12L);
            PostDocument doc2 = makeDocument(30L, "title2", "content2", 23L);
            PostDocument doc3 = makeDocument(41L, "title3", "content3", 32L);
            List<PostSearchHit> responses = List.of(
                    new PostSearchHit(doc1, 0.5f),
                    new PostSearchHit(doc2, 0.6f),
                    new PostSearchHit(doc3, 0.7f)
            );
            PostSort sort = new PostSort(1000f, 100000L, 100000L);
            PostResponse response1 = makePostResponse(doc1);
            PostResponse response2 = makePostResponse(doc2);
            PostResponse response3 = makePostResponse(doc3);

            //when
            when(mapper.documentToResponse(doc1)).thenReturn(response1);
            when(mapper.documentToResponse(doc2)).thenReturn(response2);
            when(mapper.documentToResponse(doc3)).thenReturn(response3);

            //then
            PostManager.PostListResponseFactory factory = new PostManager.PostListResponseFactory(mapper);
            PostListResponse response = factory.build(responses, sort);

            assertThat(response.getPostList()).hasSize(3);
            assertThat(response.getLastPostId()).isEqualTo(41L);
            assertThat(response.getSort().likeCnt()).isEqualTo(32L);
            assertThat(response.getSort().lastPostId()).isEqualTo(41L);
            assertThat(response.getSort()._score()).isEqualTo(0.7f);
            assertThat(response.getPostList().get(0).getPostId()).isEqualTo(20L);
            assertThat(response.getPostList().get(1).getPostId()).isEqualTo(30L);
            assertThat(response.getPostList().get(2).getPostId()).isEqualTo(41L);
        }

        @DisplayName("response 값이 비어있으면  빈 리스트 값을 반환합니다.")
        @Test
        void build_PostSearchHit_To_PostListResponse_RETURN_EMPTYLIST() {
            //given
            List<PostSearchHit> responses = new ArrayList<>();
            PostSort sort = new PostSort(1000f, 100000L, 100000L);

            //when
            PostManager.PostListResponseFactory factory = new PostManager.PostListResponseFactory(mapper);
            PostListResponse response = factory.build(responses, sort);


            //then
            assertThat(response.getPostList()).isEqualTo(Collections.EMPTY_LIST);
            assertThat(response.getSort()._score()).isEqualTo(sort._score());
            assertThat(response.getSort().lastPostId()).isEqualTo(sort.lastPostId());
            assertThat(response.getSort().likeCnt()).isEqualTo(sort.likeCnt());
            assertThat(response.getLastPostId()).isEqualTo(sort.lastPostId());

        }

        @DisplayName("SearchAutoCompleteResponse 형태로 변환합니다.")
        @Test
        void build_String_To_SearchAutoCompleteResponse() {
            //given
            List<String> responses = List.of(
                    "title1", "title2", "title3"
            );

            //when
            PostManager.PostListResponseFactory factory = new PostManager.PostListResponseFactory(mapper);
            List<SearchAutoCompleteResponse> result = factory.build(responses);

            //then
            assertThat(result).hasSize(3);
            assertThat(result.get(0).getSuggest()).isEqualTo(responses.get(0));
            assertThat(result.get(1).getSuggest()).isEqualTo(responses.get(1));
            assertThat(result.get(2).getSuggest()).isEqualTo(responses.get(2));
        }

        private PostDocument makeDocument(Long postId, String title, String content, Long likeCnt) {
            return PostDocument.builder()
                    .postId(postId)
                    .likeCnt(likeCnt)
                    .memberName("name")
                    .hashTag(HashTag.THREE_POINT.getName())
                    .title(title)
                    .content(content)
                    .highlightUrl("url")
                    .createdAt(LocalDateTime.now())
                    .modifiedAt(LocalDateTime.now())
                    .build();
        }

        private PostResponse makePostResponse(PostDocument document) {
            return PostResponse.builder()
                    .title(document.getTitle())
                    .postId(document.getPostId())
                    .hashTag(document.getHashTag())
                    .createdAt(document.getCreatedAt())
                    .memberName(document.getMemberName())
                    .modifiedAt(document.getModifiedAt())
                    .highlightUrl(document.getHighlightUrl())
                    .likeCnt(document.getLikeCnt())
                    .content(document.getContent())
                    .build();
        }

    }
}
