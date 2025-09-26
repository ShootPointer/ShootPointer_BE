package com.midas.shootpointer.domain.post.helper.elastic;

import com.midas.shootpointer.domain.post.dto.response.PostSort;
import com.midas.shootpointer.domain.post.entity.PostEntity;

import java.util.List;

public interface PostElasticSearchUtil {
    Long createPostDocument(PostEntity post);
    List<Object[]> getPostByTitleOrContentByElasticSearch(String search,int size, PostSort sort);
}
