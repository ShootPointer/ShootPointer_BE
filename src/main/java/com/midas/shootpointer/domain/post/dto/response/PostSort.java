package com.midas.shootpointer.domain.post.dto.response;

import jakarta.validation.constraints.NotNull;

public record PostSort(
        @NotNull int size,
        @NotNull Double _score,
        @NotNull Long likeCnt,
        @NotNull Long lastPostId
) {
}
