package com.midas.shootpointer.domain.post.repository;

import com.midas.shootpointer.domain.post.entity.PostDocument;

import java.util.List;

public interface PostElasticsearchCustomRepository {
    List<PostDocument>search(String search,int size);
}
