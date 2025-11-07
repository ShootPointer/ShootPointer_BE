package com.midas.shootpointer.infrastructure.redis.entity;

import lombok.Getter;

@Getter
public enum OpenCVChannels {
    UPLOAD("opencv-progress-upload"),
    HIGHLIGHT("opencv-progress-highlight");

    OpenCVChannels(String channelName){
        this.channelName=channelName;
    }

    private final String channelName;
}

