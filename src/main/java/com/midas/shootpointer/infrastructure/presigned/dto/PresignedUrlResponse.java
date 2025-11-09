package com.midas.shootpointer.infrastructure.presigned.dto;

public record PresignedUrlResponse(
        //pre-signedUrl
        String presignedUrl,
        //서명 값
        String signature,
        //JobId
        String jobId
) {
    public static PresignedUrlResponse of(String presignedUrl,String signature,String jobId){
        return new PresignedUrlResponse(presignedUrl,signature,jobId);
    }
}
