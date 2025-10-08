package com.midas.shootpointer.domain.post.repository;

import com.midas.shootpointer.domain.post.dto.response.PostSort;
import com.midas.shootpointer.domain.post.entity.PostDocument;
import org.springframework.data.elasticsearch.core.SearchHits;
public interface PostCustomElasticSearchRepository {
    SearchHits<PostDocument> search(String search, int size, PostSort sort);
    SearchHits<PostDocument> suggestCompleteByKeyword(String keyword) ;
    SearchHits<PostDocument> searchByHashTag(String search,int size, PostSort sort);
    SearchHits<PostDocument> suggestCompleteByHashTag(String keyword);
}
