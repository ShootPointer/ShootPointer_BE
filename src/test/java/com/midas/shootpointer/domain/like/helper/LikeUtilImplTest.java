package com.midas.shootpointer.domain.like.helper;

import com.midas.shootpointer.BaseSpringBootTest;
import com.midas.shootpointer.domain.like.entity.LikeEntity;
import com.midas.shootpointer.domain.like.repository.LikeCommandRepository;
import com.midas.shootpointer.domain.like.repository.LikeQueryRepository;
import com.midas.shootpointer.domain.member.entity.Member;
import com.midas.shootpointer.domain.member.repository.MemberQueryRepository;
import com.midas.shootpointer.domain.post.entity.HashTag;
import com.midas.shootpointer.domain.post.entity.PostEntity;
import com.midas.shootpointer.domain.post.repository.PostCommandRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.data.elasticsearch.ElasticsearchDataAutoConfiguration;
import org.springframework.boot.autoconfigure.data.elasticsearch.ElasticsearchRepositoriesAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("dev")
class LikeUtilImplTest extends BaseSpringBootTest {
    @Autowired
    private LikeUtilImpl likeUtil;

    @Autowired
    private MemberQueryRepository memberRepository;

    @Autowired
    private PostCommandRepository postCommandRepository;

    @Autowired
    private LikeQueryRepository likeQueryRepository;

    @Autowired
    private LikeCommandRepository likeCommandRepository;

    @BeforeEach
    void setUp(){
        likeCommandRepository.deleteAll();;
        postCommandRepository.deleteAll();
        memberRepository.deleteAll();
    }

    @AfterEach
    void cleanUp(){
        likeCommandRepository.deleteAll();;
        postCommandRepository.deleteAll();
        memberRepository.deleteAll();
    }

    @Test
    @DisplayName("좋아요의 개수가 1씩 증가합니다.")
    void increaseLikeCnt() {
        //given
        Member savedMember=memberRepository.save(makeMockMember());
        PostEntity savedPost=postCommandRepository.save(makeMockPostEntity(savedMember));

        //when
        likeUtil.increaseLikeCnt(savedPost);
        likeUtil.increaseLikeCnt(savedPost);

        //then
        assertThat(savedPost.getLikeCnt()).isEqualTo(2);
    }

    @Test
    @DisplayName("좋아요의 개수가 1씩 감소합니다.")
    void decreaseLikeCnt() {
        //given
        Member savedMember=memberRepository.save(makeMockMember());
        PostEntity savedPost=postCommandRepository.save(makeMockPostEntity(savedMember));
        likeUtil.increaseLikeCnt(savedPost);
        likeUtil.increaseLikeCnt(savedPost);
        likeUtil.increaseLikeCnt(savedPost);
        likeUtil.increaseLikeCnt(savedPost);

        //when
        likeUtil.decreaseLikeCnt(savedPost);
        likeUtil.decreaseLikeCnt(savedPost);

        //then

        assertThat(savedPost.getLikeCnt()).isEqualTo(2);
    }

    @Test
    @DisplayName("좋아요 객체를 생성하고 저장합니다.")
    void createLike() {
        //given
        Member savedMember=memberRepository.save(makeMockMember());
        PostEntity savedPost=postCommandRepository.save(makeMockPostEntity(savedMember));

        //when
        LikeEntity savedLike=likeUtil.createLike(savedPost,savedMember);

        //then
        assertThat(savedLike).isNotNull();
        assertThat(savedLike.getMember()).isEqualTo(savedMember);
        assertThat(savedLike.getPost()).isEqualTo(savedPost);
    }

    @Test
    @DisplayName("좋아요 객체를 삭제합니다.")
    void deleteLike() {
        //given
        Member savedMember=memberRepository.save(makeMockMember());
        PostEntity savedPost=postCommandRepository.save(makeMockPostEntity(savedMember));
        LikeEntity likeEntity=LikeEntity.builder()
                .post(savedPost)
                .member(savedMember)
                .build();
        likeEntity=likeCommandRepository.save(likeEntity);
        Long likeId=likeEntity.getLikeId();

        //when
        likeUtil.deleteLike(likeEntity);

        //then
        assertThat(likeQueryRepository.findById(likeId)).isEmpty();
    }

    @Test
    @DisplayName("게시물 Id와 유저 Id를 이용하여 좋아요 객체를 찾습니다.")
    void findByPostIdAndMemberId() {
        //given
        Member savedMember=memberRepository.save(makeMockMember());
        PostEntity savedPost=postCommandRepository.save(makeMockPostEntity(savedMember));
        LikeEntity likeEntity=LikeEntity.builder()
                .post(savedPost)
                .member(savedMember)
                .build();
        likeEntity=likeCommandRepository.save(likeEntity);

        //when
        LikeEntity findLike=likeUtil.findByPostIdAndMemberId(savedMember.getMemberId(),savedPost.getPostId());

        //then
        assertThat(likeEntity.getLikeId()).isEqualTo(findLike.getLikeId());
        assertThat(likeEntity.getPost().getPostId()).
                isEqualTo(findLike.getPost().getPostId());
        assertThat(likeEntity.getMember().getMemberId()).
                isEqualTo(findLike.getMember().getMemberId());
    }

    private PostEntity makeMockPostEntity(Member member){
        return PostEntity.builder()
                .member(member)
                .title("title")
                .content("content")
                .hashTag(HashTag.TREE_POINT)
                .build();
    }

    private Member makeMockMember(){
        return Member.builder()
                .email("test@naver.com")
                .username("test")
                .build();
    }


}