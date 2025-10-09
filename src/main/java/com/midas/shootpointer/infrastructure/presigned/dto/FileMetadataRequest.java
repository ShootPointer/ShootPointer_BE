package com.midas.shootpointer.infrastructure.presigned.dto;

/**
 * Pre-Signed URL 요청 시 원본 영상 meta data 정보
 */
public record FileMetadataRequest(
        String fileName,
        Long fileSize
) {
    public static FileMetadataRequest of(String fileName,Long fileSize){
        return new FileMetadataRequest(fileName, fileSize);
    }
}
