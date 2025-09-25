package com.midas.shootpointer.domain.post.elasticsearch;

import org.springframework.context.annotation.Profile;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
@Profile("!dev")  // dev 프로파일이 아닐 때만 활성화
public interface PostElasticSearchRepository extends ElasticsearchRepository<PostDocument,Long> {

}
