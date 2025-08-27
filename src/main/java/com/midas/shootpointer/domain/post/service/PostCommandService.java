package com.midas.shootpointer.domain.post.service;

import com.midas.shootpointer.domain.post.dto.PostRequest;
import com.midas.shootpointer.domain.post.dto.PostResponse;

public interface PostCommandService {
    PostResponse create(PostRequest request,String token);
}
