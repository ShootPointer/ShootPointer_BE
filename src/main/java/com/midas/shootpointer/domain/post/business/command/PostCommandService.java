package com.midas.shootpointer.domain.post.business.command;

import com.midas.shootpointer.domain.member.entity.Member;
import com.midas.shootpointer.domain.post.entity.PostEntity;

public interface PostCommandService {
    Long create(PostEntity post, Member member);

    Long update(PostEntity post, Member member,Long postId);

    Long delete(Member member,Long postId);
}
