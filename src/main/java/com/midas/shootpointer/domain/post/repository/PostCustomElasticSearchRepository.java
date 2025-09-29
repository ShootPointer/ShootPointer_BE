package com.midas.shootpointer.domain.post.repository;

import com.midas.shootpointer.domain.post.dto.response.PostSort;
import com.midas.shootpointer.domain.post.entity.PostDocument;
import org.springframework.context.annotation.Profile;
import org.springframework.data.elasticsearch.core.SearchHits;

import java.io.IOException;

@Profile("!dev")
public interface PostCustomElasticSearchRepository {
    SearchHits<PostDocument> search(String search, int size, PostSort sort);
    SearchHits<PostDocument> suggestCompleteByKeyword(String keyword) throws IOException;
}
