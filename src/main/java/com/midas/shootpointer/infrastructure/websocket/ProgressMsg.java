package com.midas.shootpointer.infrastructure.websocket;

/**
 * 진행률 메시지 DTO
 */
public record ProgressMsg(
        PROGRESS_TYPE progressType,
        double progress,
        String message
) {
    public static ProgressMsg of(PROGRESS_TYPE progressType,double progress,String message){
        return new ProgressMsg(progressType,progress,message);
    }
}
