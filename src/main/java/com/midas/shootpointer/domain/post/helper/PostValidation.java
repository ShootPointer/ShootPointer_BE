package com.midas.shootpointer.domain.post.helper;

import com.midas.shootpointer.domain.member.entity.Member;

public interface PostValidation {
    boolean isValidateHighlightUrl(Member member,String highlightUrl);
}
