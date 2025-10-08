package com.midas.shootpointer.domain.post.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.midas.shootpointer.domain.member.entity.Member;
import com.midas.shootpointer.domain.post.business.query.PostQueryService;
import com.midas.shootpointer.domain.post.dto.response.PostListResponse;
import com.midas.shootpointer.domain.post.dto.response.PostResponse;
import com.midas.shootpointer.domain.post.dto.response.PostSort;
import com.midas.shootpointer.domain.post.dto.response.SearchAutoCompleteResponse;
import com.midas.shootpointer.global.security.CustomUserDetails;
import java.util.Collections;
import java.util.UUID;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class PostQueryControllerTest {
    private MockMvc mockMvc;

    private ObjectMapper objectMapper;

    @InjectMocks
    private PostQueryController postQueryController;

    @Mock
    private PostQueryService postQueryService;

    private final String baseUrl="/api/post";

    @BeforeEach
    void setUp(){
        mockMvc= MockMvcBuilders.standaloneSetup(postQueryController)
                .build();
        objectMapper=new ObjectMapper();
    }

    @Test
    @DisplayName("게시물 단건 조회 GET 요청 성공 시 postResponse를 반환합니다._SUCCESS")
    void singleRead() throws Exception {
        //given
        Long postId=123124L;
        PostResponse response=makePostResponse(LocalDateTime.now(),postId,10L,"title","content");

        //when
        when(postQueryService.singleRead(anyLong()))
                .thenReturn(response);

        //then
        mockMvc.perform(get(baseUrl+"/"+postId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("OK"))
                .andExpect(jsonPath("$.success").value(true))

                .andExpect(jsonPath("$.data.postId").value(response.getPostId()))
                .andExpect(jsonPath("$.data.title").value(response.getTitle()))
                .andExpect(jsonPath("$.data.content").value(response.getContent()))
                .andExpect(jsonPath("$.data.highlightUrl").value(response.getHighlightUrl()))
                .andExpect(jsonPath("$.data.likeCnt").value(response.getLikeCnt()))
                .andExpect(jsonPath("$.data.createdAt").value(getCreatedDateFormat(response)))
                .andExpect(jsonPath("$.data.modifiedAt").value(getModifiedDateFormat(response)))
                .andExpect(jsonPath("$.data.hashTag").value(response.getHashTag().toString()))
                .andDo(print());

        verify(postQueryService,times(1)).singleRead(postId);

    }

    @Test
    @DisplayName("게시물 다건 조회 GET 요청 성공 시 PostListResponse를 반환합니다-인기순._SUCCESS")
    void multiRead_POPULAR() throws Exception {
        //given
        String type="popular";
        Long postId=1000L;
        int size=2;

        List<PostResponse> postResponses=new ArrayList<>();
        postResponses.add(makePostResponse(LocalDateTime.now(), 1L,50L,"",""));
        postResponses.add(makePostResponse(LocalDateTime.now(), 2L,30L,"",""));

        PostListResponse expectedResponse=PostListResponse.of(postId,postResponses);

        //when
        when(postQueryService.multiRead(postId,size,type)).thenReturn(expectedResponse);

        //then
        mockMvc.perform(get(baseUrl)
                        .param("postId",postId.toString())
                        .param("size", String.valueOf(size))
                        .param("type",type))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("OK"))
                .andExpect(jsonPath("$.success").value(true))

                .andExpect(jsonPath("$.data.postList[0].postId").value(1L))
                .andExpect(jsonPath("$.data.postList[0].likeCnt").value(50L))
                .andExpect(jsonPath("$.data.postList[1].postId").value(2L))
                .andExpect(jsonPath("$.data.postList[1].likeCnt").value(30L))
                .andDo(print());

        verify(postQueryService,times(1)).multiRead(postId,size,type);
    }

    @Test
    @DisplayName("게시물 다건 조회 GET 요청 성공 시 PostListResponse를 반환합니다-최신순._SUCCESS")
    void multiRead_LATEST() throws Exception {
        //given
        String type="latest";
        Long postId=1000L;
        int size=2;

        List<PostResponse> postResponses=new ArrayList<>();
        postResponses.add(makePostResponse(LocalDateTime.now(), 1L,50L,"",""));
        postResponses.add(makePostResponse(LocalDateTime.now().minusDays(2L), 2L,30L,"",""));

        PostListResponse expectedResponse=PostListResponse.of(postId,postResponses);

        //when
        when(postQueryService.multiRead(postId,size,type)).thenReturn(expectedResponse);

        //then
        mockMvc.perform(get(baseUrl)
                        .param("postId",postId.toString())
                        .param("size", String.valueOf(size))
                        .param("type",type))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("OK"))
                .andExpect(jsonPath("$.success").value(true))

                .andExpect(jsonPath("$.data.postList[0].postId").value(1L))
                .andExpect(jsonPath("$.data.postList[0].likeCnt").value(50L))
                .andExpect(jsonPath("$.data.postList[1].postId").value(2L))
                .andExpect(jsonPath("$.data.postList[1].likeCnt").value(30L))
                .andDo(print());

        verify(postQueryService,times(1)).multiRead(postId,size,type);
    }

    @Test
    @DisplayName("게시물 검색 조회 GET 요청 성공 시 PostListResponse를 반환합니다._SUCCESS")
    void search() throws Exception {
        //given
        String search="test";
        Long postId=1000L;
        int size=10;

        List<PostResponse> postResponses=new ArrayList<>();
        postResponses.add(makePostResponse(LocalDateTime.now(), 1L,50L,"title1","content1"));
        postResponses.add(makePostResponse(LocalDateTime.now().minusDays(2L), 2L,30L,"title2","content2"));

        PostListResponse expectedResponse=PostListResponse.of(2L,postResponses);

        //when
        when(postQueryService.search(search,postId,size)).thenReturn(expectedResponse);

        //then
        mockMvc.perform(get(baseUrl+"/list")
                        .param("postId",postId.toString())
                        .param("size", String.valueOf(size))
                        .param("search",search))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("OK"))
                .andExpect(jsonPath("$.success").value(true))

                .andExpect(jsonPath("$.data.postList[0].postId").value(1L))
                .andExpect(jsonPath("$.data.postList[0].likeCnt").value(50L))
                .andExpect(jsonPath("$.data.postList[0].title").value("title1"))
                .andExpect(jsonPath("$.data.postList[0].content").value("content1"))

                .andExpect(jsonPath("$.data.postList[1].postId").value(2L))
                .andExpect(jsonPath("$.data.postList[1].likeCnt").value(30L))
                .andExpect(jsonPath("$.data.postList[1].title").value("title2"))
                .andExpect(jsonPath("$.data.postList[1].content").value("content2"))

                .andExpect(jsonPath("$.data.lastPostId").value(2L))
                .andDo(print());

        verify(postQueryService,times(1)).search(search,postId,size);

    }

    @Test
    @DisplayName("게시물 검색 조회(Elasticsearch) GET 요청 성공 시 PostListResponse를 반환합니다._SUCCESS")
    void searchElastic() throws Exception {
        //given
        int size=10;
        float _score=1231231f;
        Long postId=2132131221L;
        Long likeCnt=12123123L;
        String search="title";
        PostSort sort=new PostSort(_score,likeCnt,postId);

        PostListResponse response=PostListResponse.withSort(
                postId,
                List.of(makePostResponse(LocalDateTime.now(),1L,123L,"title1","content1"),
                        makePostResponse(LocalDateTime.now(),2L,124L,"title2","content2")
                        ),
                sort
                );

        //when
        when(postQueryService.searchByElastic(search,size,sort)).thenReturn(response);

        //then
        mockMvc.perform(get(baseUrl+"/list-elastic")
                        .param("postId",postId.toString())
                        .param("size", String.valueOf(size))
                        .param("search",search)
                        .param("_score", String.valueOf(sort._score()))
                        .param("likeCnt",String.valueOf(likeCnt)))

                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("OK"))
                .andExpect(jsonPath("$.success").value(true))

                .andExpect(jsonPath("$.data.postList[0].postId").value(1L))
                .andExpect(jsonPath("$.data.postList[0].likeCnt").value(123L))
                .andExpect(jsonPath("$.data.postList[0].title").value("title1"))
                .andExpect(jsonPath("$.data.postList[0].content").value("content1"))

                .andExpect(jsonPath("$.data.postList[1].postId").value(2L))
                .andExpect(jsonPath("$.data.postList[1].likeCnt").value(124L))
                .andExpect(jsonPath("$.data.postList[1].title").value("title2"))
                .andExpect(jsonPath("$.data.postList[1].content").value("content2"))

                .andExpect(jsonPath("$.data.lastPostId").value(postId))
                .andDo(print());

        verify(postQueryService,times(1)).searchByElastic(search,size,sort);


    }

    @Test
    @DisplayName("게시물 검색 조회(Elasticsearch) GET 요청 성공 시 parameter = search 빈 값이면 최신순으로 게시물을 반환합니다._SUCCESS")
    void searchElastic_BLANK() throws Exception {
        //given
        int size=10;
        float _score=1231231f;
        Long postId=2132131221L;
        Long likeCnt=12123123L;
        String search="";
        PostSort sort=new PostSort(_score,likeCnt,postId);

        PostListResponse response=PostListResponse.withSort(
                postId,
                List.of(makePostResponse(LocalDateTime.now(),1L,123L,"title1","content1"),
                        makePostResponse(LocalDateTime.now(),2L,124L,"title2","content2")
                ),
                sort
        );

        //when
        when(postQueryService.multiRead(postId,size,"latest")).thenReturn(response);

        //then
        mockMvc.perform(get(baseUrl+"/list-elastic")
                        .param("postId",postId.toString())
                        .param("size", String.valueOf(size))
                        .param("search",search)
                        .param("_score", String.valueOf(sort._score()))
                        .param("likeCnt",String.valueOf(likeCnt)))

                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("OK"))
                .andExpect(jsonPath("$.success").value(true))

                .andExpect(jsonPath("$.data.postList[0].postId").value(1L))
                .andExpect(jsonPath("$.data.postList[0].likeCnt").value(123L))
                .andExpect(jsonPath("$.data.postList[0].title").value("title1"))
                .andExpect(jsonPath("$.data.postList[0].content").value("content1"))

                .andExpect(jsonPath("$.data.postList[1].postId").value(2L))
                .andExpect(jsonPath("$.data.postList[1].likeCnt").value(124L))
                .andExpect(jsonPath("$.data.postList[1].title").value("title2"))
                .andExpect(jsonPath("$.data.postList[1].content").value("content2"))

                .andExpect(jsonPath("$.data.lastPostId").value(postId))
                .andDo(print());

        verify(postQueryService,times(1)).multiRead(postId,size,"latest");



    }

    @Test
    @DisplayName("게시물 검색 자동 완성 조회(Elasticsearch) GET 요청 성공 시 SearchAutoCompleteResponse를 반환합니다._SUCCESS")
    void searchSuggest() throws Exception {
        //given
        List<SearchAutoCompleteResponse> responses = new ArrayList<>(List.of(
                SearchAutoCompleteResponse.of("title1"),
                SearchAutoCompleteResponse.of("title2"),
                SearchAutoCompleteResponse.of("title3")
        ));
        String search="title";

        //when
        when(postQueryService.suggest(search)).thenReturn(responses);

        //then
        mockMvc.perform(get(baseUrl+"/suggest")
                        .param("keyword",search))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("OK"))
                .andExpect(jsonPath("$.success").value(true))

                .andExpect(jsonPath("$.data[0].suggest").value("title1"))
                .andExpect(jsonPath("$.data[1].suggest").value("title2"))
                .andExpect(jsonPath("$.data[2].suggest").value("title3"))

                .andDo(print());

        verify(postQueryService,times(1)).suggest(search);
    }
    
    @Test
    @DisplayName("마이페이지 게시물 조회 GET 요청 성공 시 PostListResponse를 반환합니다._SUCCESS")
    void getMyPosts() throws Exception {
        //given
        UUID testMemberId = UUID.randomUUID();
        setAuthenticatedMember(testMemberId);
        
        List<PostResponse> postResponses = new ArrayList<>();
        postResponses.add(makePostResponse(LocalDateTime.now(), 1L, 50L, "title1", "content1"));
        postResponses.add(makePostResponse(LocalDateTime.now(), 2L, 30L, "title2", "content2"));
        postResponses.add(makePostResponse(LocalDateTime.now(), 3L, 20L, "title3", "content3"));
        
        PostListResponse expectedResponse = PostListResponse.of(3L, postResponses);
        
        when(postQueryService.getMyPosts(testMemberId)).thenReturn(expectedResponse);
        
        //when - then
        mockMvc.perform(get(baseUrl + "/mypage"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.status").value("OK"))
            .andExpect(jsonPath("$.success").value(true))
            
            .andExpect(jsonPath("$.data.postList[0].postId").value(1L))
            .andExpect(jsonPath("$.data.postList[0].likeCnt").value(50L))
            .andExpect(jsonPath("$.data.postList[0].title").value("title1"))
            .andExpect(jsonPath("$.data.postList[0].content").value("content1"))
            
            .andExpect(jsonPath("$.data.postList[1].postId").value(2L))
            .andExpect(jsonPath("$.data.postList[1].likeCnt").value(30L))
            .andExpect(jsonPath("$.data.postList[1].title").value("title2"))
            .andExpect(jsonPath("$.data.postList[1].content").value("content2"))
            
            .andExpect(jsonPath("$.data.postList[2].postId").value(3L))
            .andExpect(jsonPath("$.data.postList[2].likeCnt").value(20L))
            .andExpect(jsonPath("$.data.postList[2].title").value("title3"))
            .andExpect(jsonPath("$.data.postList[2].content").value("content3"))
            
            .andExpect(jsonPath("$.data.lastPostId").value(3L))
            .andDo(print());
        
        verify(postQueryService, times(1)).getMyPosts(testMemberId);
    }
    
    @Test
    @DisplayName("마이페이지 게시물 조회 시 게시물이 없으면 빈 리스트를 반환합니다._EMPTY")
    void getMyPosts_EMPTY() throws Exception {
        //given
        UUID testMemberId = UUID.randomUUID();
        setAuthenticatedMember(testMemberId);
        PostListResponse expectedResponse = PostListResponse.of(null, List.of());
        
        //when
        when(postQueryService.getMyPosts(testMemberId)).thenReturn(expectedResponse);
        
        //then
        mockMvc.perform(get(baseUrl + "/mypage"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.status").value("OK"))
            .andExpect(jsonPath("$.success").value(true))
            .andExpect(jsonPath("$.data.postList").isEmpty())
            .andExpect(jsonPath("$.data.lastPostId").doesNotExist())
            .andDo(print());
        
        verify(postQueryService, times(1)).getMyPosts(testMemberId);
    }
    
    @NotNull
    private static String getCreatedDateFormat(PostResponse response) {
        return response.getCreatedAt().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
    }

    @NotNull
    private static String getModifiedDateFormat(PostResponse response) {
        return response.getModifiedAt().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
    }

    private PostResponse makePostResponse(LocalDateTime time, Long postId,Long likeCnt,String title,String content){
        return PostResponse.builder()
                .content(content)
                .likeCnt(likeCnt)
                .createdAt(time)
                .modifiedAt(time)
                .highlightUrl("test")
                .postId(postId)
                .title(title)
                .hashTag("2점슛")
                .build();

    }
    
    private void setAuthenticatedMember(UUID memberId) {
        Member testMember = Member.builder()
            .memberId(memberId)
            .email("test@naver.com")
            .build();
        
        CustomUserDetails userDetails = new CustomUserDetails(testMember);
        
        Authentication auth = new UsernamePasswordAuthenticationToken(
            userDetails,
            null,
            userDetails.getAuthorities()
        );
        
        SecurityContextHolder.getContext().setAuthentication(auth);
    }

}