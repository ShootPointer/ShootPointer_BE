package com.midas.shootpointer.domain.post.helper;

import com.midas.shootpointer.domain.highlight.entity.HighlightEntity;
import com.midas.shootpointer.domain.highlight.repository.HighlightCommandRepository;
import com.midas.shootpointer.domain.member.entity.Member;
import com.midas.shootpointer.domain.member.repository.MemberCommandRepository;
import com.midas.shootpointer.domain.post.business.PostOrderType;
import com.midas.shootpointer.domain.post.entity.HashTag;
import com.midas.shootpointer.domain.post.entity.PostEntity;
import com.midas.shootpointer.domain.post.helper.simple.PostUtilImpl;
import com.midas.shootpointer.domain.post.mapper.PostMapper;
import com.midas.shootpointer.domain.post.repository.PostCommandRepository;
import com.midas.shootpointer.global.common.ErrorCode;
import com.midas.shootpointer.global.exception.CustomException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowableOfType;

/**
 * 통합 테스트로 진행.
 */
@SpringBootTest
@ActiveProfiles("dev")
class PostUtilImplTest {
    @Autowired
    private PostUtilImpl postUtil;

    @Autowired
    private MemberCommandRepository memberRepository;

    @Autowired
    private PostCommandRepository postCommandRepository;

    @Autowired
    private HighlightCommandRepository highlightCommandRepository;

    @Autowired
    private PostMapper postMapper;
    @Autowired
    private PostUtilImpl postUtilImpl;


    @Test
    @DisplayName("게시판 Id를 이용하여 게시물을 조회합니다._SUCCESS")
    void findPostByPostId() {
        //given
        Member member = memberRepository.save(makeMember());
        HighlightEntity highlight=highlightCommandRepository.save(makeHighlight(member));
        PostEntity post=postCommandRepository.save(makeMockPost(member,highlight));

        //when
        PostEntity findPost=postUtil.findPostByPostId(post.getPostId());

        //then
        assertThat(findPost.getPostId()).isEqualTo(post.getPostId());
        assertThat(findPost.getMember().getMemberId()).isEqualTo(member.getMemberId());
    }

    @Test
    @DisplayName("게시판 객체를 저장합니다._SUCCESS")
    void save() {
        //given
        Member member = memberRepository.save(makeMember());
        HighlightEntity highlight=highlightCommandRepository.save(makeHighlight(member));
        PostEntity post=postCommandRepository.save(makeMockPost(member,highlight));

        //when
        PostEntity savedPost=postUtil.save(post);

        //then
        assertThat(post.getPostId()).isEqualTo(savedPost.getPostId());
        assertThat(post.getMember().getMemberId()).isEqualTo(savedPost.getMember().getMemberId());
    }

    @Test
    @DisplayName("게시판 Id로 게시판 객체를 조회합니다._SUCCESS")
    void find_SUCCESS(){
        //given
        Member member = memberRepository.save(makeMember());
        HighlightEntity highlight=highlightCommandRepository.save(makeHighlight(member));
        PostEntity post=postCommandRepository.save(makeMockPost(member,highlight));
        Long postId=post.getPostId();

        //when
        PostEntity findPost=postUtil.findPostByPostId(postId);

        //then
        assertThat(findPost).isNotNull();
        assertThat(findPost.getMember().getMemberId()).isEqualTo(post.getMember().getMemberId());
        assertThat(findPost.getPostId()).isEqualTo(postId);
        assertThat(findPost.getContent()).isEqualTo(post.getContent());
        assertThat(findPost.getHashTag()).isEqualTo(post.getHashTag());
    }


    @Test
    @DisplayName("게시판 Id로 게시판 객체를 조회를 실패하면 IS_NOT_EXIST_POST 예외를 발생시킵니다._FAIL")
    void find_FAIL(){
        //given
        Long postId=123L;

        //when
        CustomException exception=catchThrowableOfType(()->
                        postUtil.findPostByPostId(postId),
                        CustomException.class
        );

        //then
        assertThat(exception).isNotNull();
        assertThat(exception.getErrorCode()).isEqualTo(ErrorCode.IS_NOT_EXIST_POST);
    }

    @Test
    @DisplayName("게시판을 수정합니다._SUCCESS")
    void update_SUCCESS() {
        //given
        Member member = memberRepository.save(makeMember());
        HighlightEntity highlight=highlightCommandRepository.save(makeHighlight(member));
        PostEntity post=postCommandRepository.save(makeMockPost(member,highlight));


        PostEntity newPost = PostEntity.builder()
                .title("title2")
                .member(member)
                .content("content2")
                .highlight(highlight)
                .hashTag(HashTag.TWO_POINT)
                .build();

        //when
        PostEntity updatedPost = postUtil.update(newPost, post, highlight);

        //then
        assertThat(updatedPost.getHashTag()).isEqualTo(newPost.getHashTag());
        assertThat(updatedPost.getContent()).isEqualTo(newPost.getContent());
        assertThat(updatedPost.getPostId()).isEqualTo(post.getPostId());
        assertThat(updatedPost.getMember().getMemberId()).isEqualTo(post.getMember().getMemberId());
        assertThat(updatedPost.getTitle()).isEqualTo(post.getTitle());
    }

    @DisplayName("게시판 Id를 이용하여 게시물을 조회합니다.-Pessimistic Lock 적용_SUCCESS")
    @Test
    void findByPostId_with_pessimisticLock(){
        //given
        Member member=memberRepository.save(makeMember());
        HighlightEntity highlight=highlightCommandRepository.save(makeHighlight(member));
        PostEntity post=postCommandRepository.save(makeMockPost(member,highlight));

        //when
        PostEntity findPost=postUtil.findByPostByPostIdWithPessimisticLock(post.getPostId());

        //then
        assertThat(findPost.getPostId()).isEqualTo(post.getPostId());
        assertThat(findPost.getMember().getMemberId()).isEqualTo(member.getMemberId());
    }

    @DisplayName("사용자가 파라미터로 입력한 게시물 조회 방식 - '좋아요순'/'최신순'의 ENUM 형태가 올바른지 판단하고 알맞은 ENUM값을 반환합니다._SUCCESS")
    @Test
    void isValidAndGetPostOrderType_SUCCESS(){
        //given
        String latestType="latest";
        String popularType="popular";

        //when
        PostOrderType latestOrderType=postUtil.isValidAndGetPostOrderType(latestType);
        PostOrderType popularOrderType=postUtil.isValidAndGetPostOrderType(popularType);

        //then
        assertThat(latestOrderType).isEqualTo(PostOrderType.latest);
        assertThat(popularOrderType).isEqualTo(PostOrderType.popular);
    }

    @DisplayName("사용자가 파라미터로 입력한 게시물 조회 방식 - '좋아요순'/'최신순'의 ENUM 형태가 올바른지 판단하고 알맞지 않으면 NOT_EXIST_ORDER_TYPE 예외를 발생시킵니다._FAIL")
    @Test
    void isValidAndGetPostOrderType_FAIL(){
        //given
        String latestType="Latest";
        String popularType="Popular";

        //when
        CustomException customExceptionOfLatest=catchThrowableOfType(()->
                postUtil.isValidAndGetPostOrderType(latestType),
                CustomException.class
        );

        CustomException customExceptionOfPopular=catchThrowableOfType(()->
                        postUtil.isValidAndGetPostOrderType(popularType),
                CustomException.class
        );

        //then
        assertThat(customExceptionOfLatest).isNotNull();
        assertThat(customExceptionOfPopular).isNotNull();

        assertThat(customExceptionOfLatest.getErrorCode()).isEqualTo(ErrorCode.NOT_EXIST_ORDER_TYPE);
        assertThat(customExceptionOfPopular.getErrorCode()).isEqualTo(ErrorCode.NOT_EXIST_ORDER_TYPE);
    }

    @Test
    @DisplayName("제목 + 내용으로 게시물을 조회하고 NoOffSet+Slice 방식으로 변환하며, postId 내림차순(게시물 업로드 최신순)으로 정렬하여 반환합니다._SUCCESS")
    void search() throws InterruptedException {
        //given
        String search="content";
        Long postId=12312525L;
        int size=10;

        Member member=memberRepository.save(makeMember());
        HighlightEntity highlight=highlightCommandRepository.save(makeHighlight(member));
        List<PostEntity> expectedPostList=new ArrayList<>();

        //더미 게시물 11개 생성.
        for (int i=0;i<11;i++){
            Thread.sleep(100);
            expectedPostList.add(postCommandRepository.save(makeMockPost(member,highlight)));
        }
        //정렬
        expectedPostList.sort((a,b)->b.getPostId().compareTo(a.getPostId()));

        //when
        List<PostEntity> findPostList=postUtilImpl.getPostEntitiesByPostTitleOrPostContent(search,postId,size);

        //then
        assertThat(findPostList).isNotEmpty();
        assertThat(findPostList.size()).isEqualTo(size);
        for (int i=0;i<10;i++){
            assertThat(expectedPostList.get(i).getPostId()).isEqualTo(findPostList.get(i).getPostId());
        }
    }
    
    @Test
    @DisplayName("memberId로 게시물 ID 목록을 조회합니다._SUCCESS")
    void findPostIdsByMemberId_SUCCESS() {
        //given
        Member member = memberRepository.save(makeMember());
        HighlightEntity highlight = highlightCommandRepository.save(makeHighlight(member));
        
        PostEntity post1 = postCommandRepository.save(makeMockPost(member, highlight));
        PostEntity post2 = postCommandRepository.save(makeMockPost(member, highlight));
        PostEntity post3 = postCommandRepository.save(makeMockPost(member, highlight));
        
        //when
        List<Long> postIds = postUtil.findPostIdsByMemberId(member.getMemberId());
        
        //then
        assertThat(postIds).isNotEmpty();
        assertThat(postIds).hasSize(3);
        assertThat(postIds).contains(post1.getPostId(), post2.getPostId(), post3.getPostId());
    }
    
    @Test
    @DisplayName("memberId로 게시물 ID 목록 조회 시 게시물이 없으면 빈 리스트를 반환합니다._EMPTY")
    void findPostIdsByMemberId_EMPTY() {
        //given
        UUID nonExistentMemberId = UUID.randomUUID();
        
        //when
        List<Long> postIds = postUtil.findPostIdsByMemberId(nonExistentMemberId);
        
        //then
        assertThat(postIds).isEmpty();
    }
    
    @Test
    @DisplayName("게시물 ID 목록으로 게시물 엔티티들을 조회합니다._SUCCESS")
    void findPostsByPostIds_SUCCESS() {
        //given
        Member member = memberRepository.save(makeMember());
        HighlightEntity highlight = highlightCommandRepository.save(makeHighlight(member));
        
        PostEntity post1 = postCommandRepository.save(makeMockPost(member, highlight));
        PostEntity post2 = postCommandRepository.save(makeMockPost(member, highlight));
        PostEntity post3 = postCommandRepository.save(makeMockPost(member, highlight));
        
        List<Long> postIds = List.of(post1.getPostId(), post2.getPostId(), post3.getPostId());
        
        //when
        List<PostEntity> posts = postUtil.findPostsByPostIds(postIds);
        
        //then
        assertThat(posts).isNotEmpty();
        assertThat(posts).hasSize(3);
        assertThat(posts).extracting(PostEntity::getPostId)
            .containsExactlyInAnyOrder(post1.getPostId(), post2.getPostId(), post3.getPostId());
    }
    
    @Test
    @DisplayName("게시물 ID 목록으로 조회 시 존재하는 게시물만 반환합니다._PARTIAL")
    void findPostsByPostIds_PARTIAL() {
        //given
        Member member = memberRepository.save(makeMember());
        HighlightEntity highlight = highlightCommandRepository.save(makeHighlight(member));
        
        PostEntity post1 = postCommandRepository.save(makeMockPost(member, highlight));
        PostEntity post2 = postCommandRepository.save(makeMockPost(member, highlight));
        
        List<Long> postIds = List.of(post1.getPostId(), post2.getPostId(), 999999L); // 999999L은 존재하지 않는 ID
        
        //when
        List<PostEntity> posts = postUtil.findPostsByPostIds(postIds);
        
        //then
        assertThat(posts).hasSize(2);
        assertThat(posts).extracting(PostEntity::getPostId)
            .containsExactlyInAnyOrder(post1.getPostId(), post2.getPostId());
    }
    
    @Test
    @DisplayName("빈 ID 목록으로 조회 시 빈 리스트를 반환합니다._EMPTY")
    void findPostsByPostIds_EMPTY() {
        //given
        List<Long> emptyPostIds = List.of();
        
        //when
        List<PostEntity> posts = postUtil.findPostsByPostIds(emptyPostIds);
        
        //then
        assertThat(posts).isEmpty();
    }

    private PostEntity makeMockPost(Member member,HighlightEntity highlight){
        return PostEntity.builder()
                .title("title")
                .member(member)
                .highlight(highlight)
                .content("content")
                .hashTag(HashTag.TREE_POINT)
                .build();
    }

    private Member makeMember(){
        return Member.builder()
                .email("test@naver.com")
                .username("tet")
                .build();
    }

    private HighlightEntity makeHighlight(Member member){
        return HighlightEntity.builder()
                .highlightKey(UUID.randomUUID())
                .member(member)
                .highlightURL("test")
                .build();
    }

}