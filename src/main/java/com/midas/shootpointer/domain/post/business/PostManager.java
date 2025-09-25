package com.midas.shootpointer.domain.post.business;

import com.midas.shootpointer.domain.highlight.entity.HighlightEntity;
import com.midas.shootpointer.domain.highlight.helper.HighlightHelper;
import com.midas.shootpointer.domain.member.entity.Member;
import com.midas.shootpointer.domain.post.dto.response.PostListResponse;
import com.midas.shootpointer.domain.post.dto.response.PostResponse;
import com.midas.shootpointer.domain.post.elasticsearch.helper.PostElasticSearchHelper;
import com.midas.shootpointer.domain.post.entity.PostEntity;
import com.midas.shootpointer.domain.post.helper.PostHelper;
import com.midas.shootpointer.domain.post.mapper.PostMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class PostManager {
    private final PostHelper postHelper;
    private final HighlightHelper highlightHelper;
    private final PostMapper postMapper;

    @Autowired(required = false)
    private PostElasticSearchHelper postElasticSearchHelper;

    @Transactional
    public Long save(Member member, PostEntity postEntity, UUID highlightId){
        /**
         * 1.하이라이트 영상 불러오기.
         */
        HighlightEntity highlightEntity=highlightHelper.findHighlightByHighlightId(highlightId);

        /*
         * 2. Highlight URL이 유저의 영상으로 일치 여부.
         */
        postHelper.isValidateHighlightId(member,highlightId);

        /*
         * 3. 해시태그가 올바른 지 여부.
         */
        postHelper.isValidPostHashTag(postEntity.getHashTag());

        /**
         * 4. 하이라이트 저장.
         */
        postEntity.setHighlight(highlightEntity);

        /*
         * 5. 게시물 ElasticSearch Document 저장 (조건부).
         */
        if (postElasticSearchHelper != null) {
            postElasticSearchHelper.createPostDocument(postEntity);
        }


        return postHelper.save(postEntity).getPostId();
    }

    @Transactional
    public Long update(PostEntity newPost,Member member,Long postId){
        UUID highlightId=newPost.getHighlight().getHighlightId();
        /**
         * 1. 게시물이 존재하는 지 여부
         */
        PostEntity existedPost=postHelper.findPostByPostId(postId);

        /**
         * 2. 게시물이 멤버의 게시물인지 확인
         */
        postHelper.isMembersPost(existedPost,member);

        /**
         * 3.하이라이트 영상 불러오기.
         */
        HighlightEntity highlightEntity=highlightHelper.findHighlightByHighlightId(highlightId);

        /**
         * 4. Highlight URL이 유저의 영상으로 일치 여부.
         */
        postHelper.isValidateHighlightId(member,highlightId);

        /**
         * 5. 해시태그가 올바른 지 여부.
         */
        postHelper.isValidPostHashTag(newPost.getHashTag());

        /**
         * 6. 수정 진행
         */
        existedPost=postHelper.update(newPost,existedPost,highlightEntity);

        return existedPost.getPostId();
    }

    @Transactional
    public Long delete(Long postId,Member member){
        /**
         * 1. 게시물이 존재하는 지 여부
         */
        PostEntity postEntity=postHelper.findPostByPostId(postId);


        /**
         * 2. 게시물이 멤버의 게시물인지 확인
         */
        postHelper.isMembersPost(postEntity,member);

        /**
         * 3. 논리적 삭제 처리
         */
        postEntity.delete();
        return postEntity.getPostId();
    }

    @Transactional(readOnly = true)
    public PostResponse singleRead(Long postId){
        /**
         * 1. postMapper : entity -> dto 변환.
         */
        return postMapper.entityToDto(postHelper.findPostByPostId(postId));
    }

    @Transactional(readOnly = true)
    public PostListResponse multiRead(Long lastPostId,String type,int size){
        /**
         * 1. type 올바른지 확인.
         */
        PostOrderType orderType=postHelper.isValidAndGetPostOrderType(type);

        /**
         * 2. type = POPULAR (인기순) / LATEST (최신순) 정렬 후 조회.
         */
        PostListResponse response = null;
        switch (orderType){
            case popular -> response=postMapper.
                    entityToDto(postHelper.getPopularPostListBySliceAndNoOffset(lastPostId,size));

            case latest -> response=postMapper.
                    entityToDto(postHelper.getLatestPostListBySliceAndNoOffset(lastPostId,size));
        }
        return response;
    }

    @Transactional(readOnly = true)
    public PostListResponse getPostEntitiesByPostTitleOrPostContent(String search,Long postId,int size){
        /**
         * 1. 제목 + 내용 게시물 조회.
         */
        return postMapper.entityToDto(postHelper.getPostEntitiesByPostTitleOrPostContent(search,postId,size));
    }

    @Transactional(readOnly = true)
    public PostListResponse getPostByPostTitleOrPostContentByElasticSearch(String search,Long postId,int size){
        // ElasticSearch가 사용 가능한 경우에만 실행
        if (postElasticSearchHelper != null) {
            List<PostResponse> elasticSearch =
                    postElasticSearchHelper.getPostByTitleOrContentByElasticSearch(search, postId, size);

            if (!elasticSearch.isEmpty()){
                return PostListResponse.of(elasticSearch.get(elasticSearch.size()-1).getPostId(),elasticSearch);
            }
        }
        
        // ElasticSearch가 없거나 결과가 없는 경우 일반 검색
        return getPostEntitiesByPostTitleOrPostContent(search, postId, size);
    }

}
