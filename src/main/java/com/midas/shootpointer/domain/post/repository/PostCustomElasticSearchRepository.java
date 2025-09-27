package com.midas.shootpointer.domain.post.repository;

import co.elastic.clients.elasticsearch.core.SearchResponse;
import com.midas.shootpointer.domain.post.dto.response.PostSort;
import com.midas.shootpointer.domain.post.entity.PostDocument;
import org.springframework.context.annotation.Profile;
import org.springframework.data.elasticsearch.core.SearchHits;

import java.io.IOException;

@Profile("!dev")
public interface PostCustomElasticSearchRepository {
    SearchHits<PostDocument> search(String search, int size, PostSort sort);
    SearchResponse<PostDocument> suggestCompleteByKeyword(String keyword) throws IOException;
}
