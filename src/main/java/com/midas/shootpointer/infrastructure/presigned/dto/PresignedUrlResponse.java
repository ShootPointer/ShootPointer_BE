package com.midas.shootpointer.infrastructure.presigned.dto;

import java.util.UUID;

public record PresignedUrlResponse(
        //pre-signedUrl
        String presignedUrl,
        //만료 시간
        long expiredAt,
        //서명 값
        String signature,
        //Job ID
        UUID jobId
) {
    public static PresignedUrlResponse of(String presignedUrl,long expiredAt,String signature,UUID jobId){
        return new PresignedUrlResponse(presignedUrl,expiredAt,signature,jobId);
    }
}
