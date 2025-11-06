package com.midas.shootpointer.infrastructure.redis.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
@AllArgsConstructor
public enum OpenCVChannels {
    UPLOAD("opencv-progress-upload"),
    HIGHLIGHT("opencv-progress-highlight");

    private final String channelName;
}

