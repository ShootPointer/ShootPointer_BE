package com.midas.shootpointer.domain.post.helper.elastic;

import com.midas.shootpointer.domain.post.dto.response.PostSearchHit;
import com.midas.shootpointer.domain.post.dto.response.PostSort;
import com.midas.shootpointer.domain.post.entity.PostDocument;
import com.midas.shootpointer.domain.post.entity.PostEntity;
import com.midas.shootpointer.domain.post.mapper.PostElasticSearchMapper;
import com.midas.shootpointer.domain.post.repository.PostElasticSearchRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@RequiredArgsConstructor
@Component
@Profile("es")
public class PostElasticSearchUtilImpl implements PostElasticSearchUtil{
    private final PostElasticSearchRepository postElasticSearchRepository;
    private final PostElasticSearchMapper mapper;

    @Transactional
    @Override
    public Long createPostDocument(PostEntity post) {
        PostDocument postDocument=mapper.entityToDoc(post);
        return postElasticSearchRepository.save(postDocument).getPostId();
    }

    @Transactional(readOnly = true)
    @Override
    public List<PostSearchHit> getPostByTitleOrContentByElasticSearch(String search, int size, PostSort sort) {
        SearchHits<PostDocument> documentList=postElasticSearchRepository.search(search,size,sort);

        /**
         * 조회된 Document가 없는 경우 빈 리스트 반환
         */
        if(documentList==null) return Collections.emptyList();

        List<PostSearchHit> responses=new ArrayList<>();
        for (SearchHit<PostDocument> hit:documentList){
            PostDocument doc=hit.getContent();
            float _score=hit.getScore();
            //PostResponse 값 , _score 값 저장
            responses.add(new PostSearchHit(doc,_score));
        }

        return responses;
    }

    @Override
    @Transactional(readOnly = true)
    public List<String> suggestCompleteSearch(String keyword)  {

        try {
            SearchHits<PostDocument> postDocumentSearchHits=postElasticSearchRepository.suggestCompleteByKeyword(keyword);
            return postDocumentSearchHits
                    .map(hit->hit.getContent().getTitle()).toList();

        }catch (IOException e){
            return Collections.emptyList();
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<PostSearchHit> getPostByHashTagByElasticSearch(String search, int size, PostSort sort) {
        SearchHits<PostDocument> postDocumentSearchHits=postElasticSearchRepository.searchByHashTag(search,size,sort);

        /*
        * 조회된 값이 없으면 빈 리스트 반환
         */
        if (postDocumentSearchHits==null) return Collections.emptyList();

        List<PostSearchHit> responses=new ArrayList<>();
        for (SearchHit<PostDocument> hit:postDocumentSearchHits){
            PostDocument doc=hit.getContent();
            float _score=hit.getScore();
            //PostResponse 값 , _score 값 저장
            responses.add(new PostSearchHit(doc,_score));
        }

        return responses;
    }

    @Override
    @Transactional(readOnly = true)
    public List<String> suggestCompleteSearchWithHashTag(String hashTag) {
        SearchHits<PostDocument> postDocumentSearchHits=postElasticSearchRepository.suggestCompleteByHashTag(hashTag);

        /*
         * 조회된 값이 없으면 빈 리스트 반환
         */
        if (postDocumentSearchHits==null) return Collections.emptyList();

        //중복을 제거하여 5개까지 반환.
        return postDocumentSearchHits.stream()
                .map(hit->hit.getContent().getHashTag())
                .distinct()
                .limit(5)
                .toList();
    }

    /**
     * #해시태그 형식의 입력값 #제거
     * @param hashTag 입력값
     * @return #제거된 입력값
     */
    @Override
    public String refinedHashTag(String hashTag) {
        return hashTag.replaceFirst("^#", "");//맨 앞의 #제거.
    }

}
