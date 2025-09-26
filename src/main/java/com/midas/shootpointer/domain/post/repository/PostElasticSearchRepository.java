package com.midas.shootpointer.domain.post.repository;

import com.midas.shootpointer.domain.post.entity.PostDocument;
import org.springframework.context.annotation.Profile;
import org.springframework.data.elasticsearch.annotations.Query;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
@Repository
@Profile("!dev")  // dev 프로파일이 아닐 때만 활성화
public interface PostElasticSearchRepository extends ElasticsearchRepository<PostDocument, Long> {
    /**
     * 조건
     * 1. 제목 search 포함 : 가중치 +30
     * 2. 내용 search 포함 : 가중치 +10
     * 3. Elastic Search score 내림차순
     * 4. 점수 동일 시, like_cnt 내림차순
     * 5. score, like_cnt 동일 시 post_id 내림차순
     */
    @Query("""
            {
                  "bool": {
                    "should": [
                      {
                        "match": {
                          "title": {
                            "query": "?0",
                            "boost": 30
                          }
                        }
                      },
                      {
                        "match": {
                          "content": {
                            "query": "?0",
                            "boost": 10
                          }
                        }
                      }
                    ]
                  },
                  "sort":[
                    {"_score" : {"order": "desc"} },
                    {"likeCnt" : {"order": "desc} },
                    {"postId" : {"order": "desc"} }
                  ],
                  "size": "?1",
                  "search_after": ["?2","?3","?4"]
                }
            """)
    /*==========================
    *
    *PostElasticSearchRepository
    *
    * @parm [
    *           search : 검색어
    *           size : 불러올 게시물 크기
    *           _score : 마지막 게시물 _score
    *           likeCnt : 마지막 게시물 좋아요 개수
    *           lastPostId : 마지막 게시물 게시물 id
    * ]
    * @return java.util.List<com.midas.shootpointer.domain.post.entity.PostDocument>
    * @author kimdoyeon
    * @version 1.0.0
    * @date 25. 9. 26.
    *
    ==========================**/
    List<PostDocument> search(String search, int size,double _score,Long likeCnt,Long lastPostId);
}
