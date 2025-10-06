package com.midas.shootpointer.domain.post.repository;

import com.midas.shootpointer.domain.post.entity.PostDocument;
import org.springframework.context.annotation.Profile;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

@Profile("es")
public interface PostElasticSearchRepository extends ElasticsearchRepository<PostDocument, Long>,PostCustomElasticSearchRepository {
}
