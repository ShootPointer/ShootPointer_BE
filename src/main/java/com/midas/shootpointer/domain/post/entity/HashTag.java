package com.midas.shootpointer.domain.post.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum HashTag {
    TWO_POINT("2점슛"),
    THREE_POINT("3점슛");

    private final String name;
}
