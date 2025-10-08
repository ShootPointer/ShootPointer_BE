package com.midas.shootpointer.domain.post.helper.elastic;

import com.midas.shootpointer.domain.post.dto.response.PostSearchHit;
import com.midas.shootpointer.domain.post.dto.response.PostSort;
import com.midas.shootpointer.domain.post.entity.PostEntity;

import java.util.List;

public interface PostElasticSearchUtil {
    Long createPostDocument(PostEntity post);
    List<PostSearchHit> getPostByTitleOrContentByElasticSearch(String search, int size, PostSort sort);
    List<String> suggestCompleteSearch(String keyword);
    List<PostSearchHit> getPostByHashTagByElasticSearch(String search, int size, PostSort sort);
    List<String> suggestCompleteSearchWithHashTag(String hashTag);
    String refinedHashTag(String hashTag);
}
