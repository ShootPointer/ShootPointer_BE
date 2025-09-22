package com.midas.shootpointer.domain.post.elasticsearch;

import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

public interface PostElasticSearchRepository extends ElasticsearchRepository<PostDocument,Long> {

}
