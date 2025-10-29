package com.midas.shootpointer.domain.post.mapper;

import com.midas.shootpointer.domain.highlight.entity.HighlightEntity;
import com.midas.shootpointer.domain.highlight.repository.HighlightCommandRepository;
import com.midas.shootpointer.domain.member.entity.Member;
import com.midas.shootpointer.domain.member.repository.MemberQueryRepository;
import com.midas.shootpointer.domain.post.dto.request.PostRequest;
import com.midas.shootpointer.domain.post.dto.response.PostListResponse;
import com.midas.shootpointer.domain.post.dto.response.PostResponse;
import com.midas.shootpointer.domain.post.entity.HashTag;
import com.midas.shootpointer.domain.post.entity.PostDocument;
import com.midas.shootpointer.domain.post.entity.PostEntity;
import com.midas.shootpointer.domain.post.repository.PostCommandRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("test")
class PostMapperImplTest  {
    @Autowired
    private PostMapperImpl postMapper;

    @Autowired
    private MemberQueryRepository memberRepository;

    @Autowired
    private PostCommandRepository postCommandRepository;

    @Autowired
    private HighlightCommandRepository highlightCommandRepository;

    @Test
    @DisplayName("Dto를 Entity로 매핑합니다.")
    void dtoToEntity_SUCCESS(){
        //given
        String content="test_test_test_test_test_test_test_test_test_test_test_test_";
        String title="title";
        HashTag hashTag=HashTag.TREE_POINT;
        UUID highlightId=UUID.randomUUID();
        PostRequest request=PostRequest.of(highlightId,title,content,hashTag);
        Member mock=makeMockMember();


        //when
        PostEntity postEntity=postMapper.dtoToEntity(request,mock);

        //then
        assertThat(postEntity.getContent()).isEqualTo(content);
        assertThat(postEntity.getHashTag()).isEqualTo(hashTag);
        assertThat(postEntity.getTitle()).isEqualTo(title);
        assertThat(postEntity.getMember()).isEqualTo(mock);
    }

    @Test
    @DisplayName("Entity를 Dto로 매핑합니다.")
    void entityToDto_SUCCESS(){
        //given
        String content="test_test_test_test_test_test_test_test_test_test_test_test_";
        String title="title";

        Member mock=memberRepository.save(makeMockMember());
        HighlightEntity highlight=highlightCommandRepository.save(HighlightEntity.builder()
                .highlightKey(UUID.randomUUID())
                .member(mock)
                .highlightURL("testst")
                .build());

        PostEntity postEntity=PostEntity.builder()
                .title(title)
                .content(content)
                .highlight(highlight)
                .hashTag(HashTag.TREE_POINT)
                .member(mock)
                .build();



        //when
        PostResponse postResponse=postMapper.entityToDto(postEntity);

        //then
        assertThat(postResponse.getContent()).isEqualTo(postEntity.getContent());
        assertThat(postResponse.getHashTag()).isEqualTo(postEntity.getHashTag().getName());
        assertThat(postResponse.getHighlightUrl()).isEqualTo(postEntity.getHighlight().getHighlightURL());
        assertThat(postResponse.getTitle()).isEqualTo(postEntity.getTitle());
        assertThat(postResponse.getLikeCnt()).isEqualTo(postEntity.getLikeCnt());
        assertThat(postResponse.getPostId()).isEqualTo(postEntity.getPostId());
        assertThat(postResponse.getCreatedAt()).isEqualTo(postEntity.getCreatedAt());
        assertThat(postResponse.getModifiedAt()).isEqualTo(postEntity.getModifiedAt());
    }

    @Test
    @DisplayName("리스트 형태의 게시물 Entity를 PostListResponseDto로 변환합니다.")
    void entityToListDto_SUCCESS(){
        /*
        * given
        */
        Member member=memberRepository.save(makeMockMember());
        HighlightEntity highlight=highlightCommandRepository.save(makeHighlight(member));
        List<PostEntity> postEntities=new ArrayList<>();
        int size=10;

        for (int i=0;i<size;i++){
            PostEntity post=postCommandRepository.save(makePostEntity(member,highlight));
            postEntities.add(post);
        }
        Long lastPostId=postEntities.get(size-1).getPostId();

        /*
        * when
        */
        PostListResponse postResponse=postMapper.entityToDto(postEntities);

        /*
        * then
        */
        assertThat(postResponse.getLastPostId()).isEqualTo(lastPostId);

        List<PostResponse> mappedPostResponse=postResponse.getPostList();
        int idx=0;
        for (PostResponse response:mappedPostResponse){
            assertThat(response.getPostId()).isEqualTo(postEntities.get(idx).getPostId());
            assertThat(response.getContent()).isEqualTo(postEntities.get(idx).getContent());
            assertThat(response.getCreatedAt()).isEqualTo(postEntities.get(idx).getCreatedAt());
            assertThat(response.getMemberName()).isEqualTo(postEntities.get(idx).getMember().getUsername());
            assertThat(response.getLikeCnt()).isEqualTo(postEntities.get(idx).getLikeCnt());
            assertThat(response.getModifiedAt()).isEqualTo(postEntities.get(idx).getModifiedAt());
            assertThat(response.getTitle()).isEqualTo(postEntities.get(idx).getTitle());
            assertThat(response.getHashTag()).isEqualTo(postEntities.get(idx).getHashTag().getName());
            assertThat(response.getHighlightUrl()).isEqualTo(postEntities.get(idx).getHighlight().getHighlightURL());
            idx++;
        }
    }

    @Test
    @DisplayName("리스트 형태의 게시물 Entity를 PostListResponseDto로 변환 시 비어있는 값이면 빈 리스트로 반환합니다. ")
    void entityToListDto_FAILED(){
        /*
         * given
         */
        Member member=memberRepository.save(makeMockMember());
        List<PostEntity> postEntities=new ArrayList<>();

        /*
         * when
         */
        PostListResponse postResponse=postMapper.entityToDto(postEntities);

        /*
         * then
         */
        assertThat(postResponse.getLastPostId()).isEqualTo(922337203685477580L);
        assertThat(postResponse.getPostList()).hasSize(0);
        assertThat(postResponse.getPostList()).isEmpty();

    }


    @Test
    @DisplayName("PostDocument를 PostResponse 형태로 변환합니다.")
    void documentToResponse(){
        //given
        LocalDateTime dateTime=LocalDateTime.now();
        PostDocument mockDocument=makePostDocument(dateTime);
        PostResponse expectedResponse=PostResponse.builder()
                .title("title")
                .modifiedAt(dateTime)
                .createdAt(dateTime)
                .memberName("name")
                .highlightUrl("url")
                .likeCnt(100L)
                .postId(1L)
                .hashTag("3점슛")
                .content("content")
                .build();

        //when
        PostResponse getResponse=postMapper.documentToResponse(mockDocument);

        //then
        assertThat(getResponse.getHighlightUrl()).isEqualTo(expectedResponse.getHighlightUrl());
        assertThat(getResponse.getHashTag()).isEqualTo(expectedResponse.getHashTag());
        assertThat(getResponse.getTitle()).isEqualTo(expectedResponse.getTitle());
        assertThat(getResponse.getModifiedAt()).isEqualTo(expectedResponse.getModifiedAt());
        assertThat(getResponse.getCreatedAt()).isEqualTo(expectedResponse.getCreatedAt());
        assertThat(getResponse.getLikeCnt()).isEqualTo(expectedResponse.getLikeCnt());
        assertThat(getResponse.getMemberName()).isEqualTo(expectedResponse.getMemberName());
        assertThat(getResponse.getPostId()).isEqualTo(expectedResponse.getPostId());
        assertThat(getResponse.getContent()).isEqualTo(expectedResponse.getContent());

    }
    /**
     * Mock Member
     */
    private Member makeMockMember(){
        return Member.builder()
                .email("test@naver.com")
                .username("teest")
                .build();
    }

    private PostEntity makePostEntity(Member member,HighlightEntity highlight){
        return PostEntity.builder()
                .likeCnt(10L)
                .hashTag(HashTag.TWO_POINT)
                .member(member)
                .title("title")
                .content("content")
                .highlight(highlight)
                .build();
    }

    private HighlightEntity makeHighlight(Member member){
        return HighlightEntity.builder()
                .highlightURL("test_test_tes")
                .highlightKey(UUID.randomUUID())
                .member(member)
                .build();
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