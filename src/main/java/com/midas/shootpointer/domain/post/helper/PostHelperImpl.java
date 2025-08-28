package com.midas.shootpointer.domain.post.helper;

import com.midas.shootpointer.domain.member.entity.Member;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PostHelperImpl implements PostHelper{
    private final PostValidation postValidation;

    @Override
    public boolean isValidateHighlightUrl(Member member, String highlightUrl) {
        return postValidation.isValidateHighlightUrl(member,highlightUrl);
    }
}
