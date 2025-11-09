package com.midas.shootpointer.domain.post.business;

import com.midas.shootpointer.domain.highlight.entity.HighlightEntity;
import com.midas.shootpointer.domain.highlight.helper.HighlightHelper;
import com.midas.shootpointer.domain.member.entity.Member;
import com.midas.shootpointer.domain.post.dto.response.*;
import com.midas.shootpointer.domain.post.entity.PostDocument;
import com.midas.shootpointer.domain.post.helper.elastic.PostElasticSearchHelper;
import com.midas.shootpointer.domain.post.entity.PostEntity;
import com.midas.shootpointer.domain.post.helper.simple.PostHelper;
import com.midas.shootpointer.domain.post.mapper.PostMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
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
    public PostListResponse getPostByPostTitleOrPostContentByElasticSearch(String search,int size,PostSort sort){
        PostListResponseFactory factory=new PostListResponseFactory(postMapper);

        /**
         * 0. ElasticSearch가 사용 가능한 경우에만 실행
          */
        if (postElasticSearchHelper == null) {
            //일반 검색
            return getPostEntitiesByPostTitleOrPostContent(search, sort.lastPostId(), size);
        }

        /**
         * 1. size 및 검색값 유효성 검증.
         */
        postHelper.isValidSize(size);
        if (!postHelper.isValidInput(search)){
            //빈 값인 경우 -> 빈 리스트 반환.
            return PostListResponse.withSort(sort.lastPostId(), Collections.emptyList(),sort);
        }

        /**
         * 2. 해시태그 기반인지 일반 검색어 기반인지 확인
         * 해시태그 기반인 경우
         */
        if (postElasticSearchHelper.isHashTagSearch(search)){
            String cleanedSearch=postElasticSearchHelper.refinedHashTag(search);
            List<PostSearchHit> postByHashTagByElasticSearch = postElasticSearchHelper.getPostByHashTagByElasticSearch(cleanedSearch, size, sort);

            return factory.build(postByHashTagByElasticSearch,sort);
        }

        /**
         * 3. 일반 검색어 기반 게시물 검색 조회
         *    게시물 정렬 조건 + 검색어 게시물 검색 , _score 조회
         */
        List<PostSearchHit> responses=postElasticSearchHelper.getPostByTitleOrContentByElasticSearch(search,size,sort);

        return factory.build(responses,sort);
    }

    @Transactional(readOnly = true)
    public List<SearchAutoCompleteResponse> suggest(String keyword){
        PostListResponseFactory factory=new PostListResponseFactory(postMapper);
        /**
         * 0. ElasticSearch가 사용 가능한 경우에만 실행
         */
        if (postElasticSearchHelper == null) {
            //일반 검색
            return Collections.emptyList();
        }

        /**
         * 1.검색값 유효성 검증.
         */
        if (!postHelper.isValidInput(keyword)){
            //빈 값인 경우 -> 빈 리스트 반환.
            return Collections.emptyList();
        }

        /*
          2. 해시태그 기반인지 일반 검색어 기반인지 확인
          해시태그 기반인 경우
         */
        if (postElasticSearchHelper.isHashTagSearch(keyword)){
            String cleanedKeyword=postElasticSearchHelper.refinedHashTag(keyword);
            List<String> postByHashTagByElasticSearch = postElasticSearchHelper.suggestCompleteSearchWithHashTag(cleanedKeyword);

            return factory.build(postByHashTagByElasticSearch);
        }

        /**
         * 3. 일반 검색어 자동 완성
         */

        List<String> postByHashTagByElasticSearch = postElasticSearchHelper.suggestCompleteSearch(keyword);

        return factory.build(postByHashTagByElasticSearch);
    }
    
    @Transactional(readOnly = true)
    public PostListResponse getMyPosts(UUID memberId) {
        List<Long> postIds = postHelper.findPostIdsByMemberId(memberId);

        if (postIds.isEmpty()) {
            return PostListResponse.of(null, Collections.emptyList());
        }

        List<PostEntity> postEntities = postHelper.findPostsByPostIds(postIds);
        List<PostResponse> postResponses = postEntities.stream()
            .map(postMapper::entityToDto)
            .toList();
        
        Long lastPostId = postEntities.isEmpty() ? null : postEntities.get(postEntities.size() - 1).getPostId();
        
        return PostListResponse.of(lastPostId, postResponses);
    }

    @Transactional(readOnly = true)
    public PostListResponse getMyLikedPosts(UUID memId,Long lastPostId,int size){
        //1. 유저가 좋아요 누른 게시물 조회
        List<PostEntity> postEntities=postHelper.getMyLikedPost(memId,lastPostId,size);

        //2. entity -> dto 변환
        return postMapper.entityToDto(postEntities);
    }

    /**
     * List<PostSearchHit> -> PostListResponse 변환 inner class
     */
    @RequiredArgsConstructor
    static class PostListResponseFactory{

        private final PostMapper mapper;
        public PostListResponse build(List<PostSearchHit> responses,PostSort sort){
            if (responses.isEmpty()){
                //빈 값인 경우 -> 빈 리스트 반환.
                return PostListResponse.withSort(sort.lastPostId(), Collections.emptyList(),sort);
            }

            //결과값이 존재하는 경우 - 마지막 게시물의 정렬 기준 전송
            int last=responses.size()-1;

            PostDocument lastResponse=responses.get(last).doc();
            PostSort newSort=new PostSort(responses.get(last)._score(),
                    lastResponse.getLikeCnt(),
                    lastResponse.getPostId()
            );

            /**
             *  List<PostDocument> -> List<PostResponse> 형태로 변환
             */
            List<PostResponse> postResponses=responses.stream()
                    .map(hit->mapper.documentToResponse(hit.doc()))
                    .toList();

            return PostListResponse.withSort(lastResponse.getPostId(),postResponses,newSort);
        }
        public List<SearchAutoCompleteResponse> build(List<String> responses){
            /**
             * 1. responses가 빈 값인지 확인
             */
            if (responses==null) return Collections.emptyList();

            return responses.stream()
                    .map(SearchAutoCompleteResponse::of)
                    .toList();
        }

    }

}
