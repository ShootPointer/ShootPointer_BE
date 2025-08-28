package com.midas.shootpointer.domain.post.helper;

import com.midas.shootpointer.domain.member.entity.Member;
import com.midas.shootpointer.domain.post.repository.PostQueryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PostValidationImpl implements PostValidation{
    private final PostQueryRepository queryRepository;

    @Override
    public boolean isValidateHighlightUrl(Member member, String highlightUrl) {
        return false;
    }
}
