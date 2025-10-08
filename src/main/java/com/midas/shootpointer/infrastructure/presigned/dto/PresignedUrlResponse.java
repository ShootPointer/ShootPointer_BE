package com.midas.shootpointer.infrastructure.presigned.dto;

import java.time.LocalDateTime;


public record PresignedUrlResponse(
        //pre-signedUrl
        String presignedUrl,
        //만료 시간
        LocalDateTime expiredAt,
        //서명 값
        String signature
) {
    public static PresignedUrlResponse of(String presignedUrl,LocalDateTime expiredAt,String signature){
        return new PresignedUrlResponse(presignedUrl,expiredAt,signature);
    }
}
