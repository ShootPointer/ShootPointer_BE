package com.midas.shootpointer.domain.post.helper;

import com.midas.shootpointer.domain.member.entity.Member;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class PostHelperImpl implements PostHelper{
    private final PostValidation postValidation;

    @Override
    public void isValidateHighlightId(Member member, UUID highlightId) {
        postValidation.isValidateHighlightId(member,highlightId);
    }

    @Override
    public void isValidPostHashTag(Object o) {
        postValidation.isValidPostHashTag(o);
    }
}
