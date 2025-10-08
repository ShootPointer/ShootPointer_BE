package com.midas.shootpointer.infrastructure.presigned.dto;

public record PresignedUrlResponse(
        //pre-signedUrl
        String presignedUrl,
        //만료 시간
        long expiredAt,
        //서명 값
        String signature
) {
    public static PresignedUrlResponse of(String presignedUrl,long expiredAt,String signature){
        return new PresignedUrlResponse(presignedUrl,expiredAt,signature);
    }
}
