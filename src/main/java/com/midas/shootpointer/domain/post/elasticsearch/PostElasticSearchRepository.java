package com.midas.shootpointer.domain.post.elasticsearch;

import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PostElasticSearchRepository extends ElasticsearchRepository<PostDocument,Long> {
}
