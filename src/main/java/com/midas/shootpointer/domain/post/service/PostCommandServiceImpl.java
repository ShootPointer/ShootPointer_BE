package com.midas.shootpointer.domain.post.service;

import com.midas.shootpointer.domain.post.dto.PostRequest;
import com.midas.shootpointer.domain.post.dto.PostResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PostCommandServiceImpl implements PostCommandService{

    @Override
    public PostResponse create(PostRequest request, String token) {
        return null;
    }
}
