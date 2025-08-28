package com.midas.shootpointer.domain.post.business;

import com.midas.shootpointer.domain.member.entity.Member;
import com.midas.shootpointer.domain.post.dto.PostRequest;

public interface PostCommandService {
    Long create(PostRequest request, Member member);
}
