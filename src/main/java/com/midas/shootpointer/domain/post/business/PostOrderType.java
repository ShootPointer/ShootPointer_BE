package com.midas.shootpointer.domain.post.business;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum PostOrderType {
    //인기순
    POPULAR,
    //최신순
    LATEST;
}
