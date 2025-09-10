package com.midas.shootpointer.domain.post.helper;

import com.midas.shootpointer.domain.member.entity.Member;
import com.midas.shootpointer.domain.post.business.PostOrderType;
import com.midas.shootpointer.domain.post.entity.HashTag;
import com.midas.shootpointer.domain.post.entity.PostEntity;

import java.util.UUID;

public interface PostValidation {
    void isValidateHighlightId(Member member, UUID highlightId);
    void isValidPostHashTag(Object o);
    void isMembersPost(PostEntity postEntity,Member member);
    PostOrderType isValidPostOrderType(String type);
}
