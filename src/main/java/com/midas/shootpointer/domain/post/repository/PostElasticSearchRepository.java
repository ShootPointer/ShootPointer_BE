package com.midas.shootpointer.domain.post.repository;

import com.midas.shootpointer.domain.post.entity.PostDocument;
import org.springframework.context.annotation.Profile;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

@Profile("!dev")  // dev 프로파일이 아닐 때만 활성화
public interface PostElasticSearchRepository extends ElasticsearchRepository<PostDocument, Long>,PostCustomElasticSearch {
}
