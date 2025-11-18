package com.midas.shootpointer.domain.post.business.command;

import com.midas.shootpointer.domain.member.entity.Member;
import com.midas.shootpointer.domain.post.dto.request.PostRequest;
import com.midas.shootpointer.domain.post.entity.PostEntity;

public interface PostCommandService {
    Long create(PostRequest request, Member member);

    Long update(PostEntity post, Member member,Long postId);

    Long delete(Member member,Long postId);
}
