package com.midas.shootpointer.domain.post.repository;

import com.midas.shootpointer.domain.post.dto.response.PostSort;
import com.midas.shootpointer.domain.post.entity.PostDocument;
import org.springframework.context.annotation.Profile;
import org.springframework.data.elasticsearch.core.SearchHits;
@Profile("!dev")
public interface PostCustomElasticSearch {
    SearchHits<PostDocument> search(String search, int size, PostSort sort);
}
