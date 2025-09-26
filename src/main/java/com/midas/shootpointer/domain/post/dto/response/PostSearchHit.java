package com.midas.shootpointer.domain.post.dto.response;

import com.midas.shootpointer.domain.post.entity.PostDocument;

public record PostSearchHit(
        PostDocument doc,
        float _score
) {
}
