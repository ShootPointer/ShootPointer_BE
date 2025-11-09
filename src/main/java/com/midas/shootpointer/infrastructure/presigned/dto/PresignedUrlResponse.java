package com.midas.shootpointer.infrastructure.presigned.dto;

public record PresignedUrlResponse(
        //pre-signedUrl
        String presignedUrl,
        //서명 값
        String signature
) {
    public static PresignedUrlResponse of(String presignedUrl,String signature){
        return new PresignedUrlResponse(presignedUrl,signature);
    }
}
