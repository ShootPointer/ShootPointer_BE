package com.midas.shootpointer.infrastructure.presigned.dto;

/**
 * Pre-Signed URL 요청 시 원본 영상 meta data 정보
 */
public record FileMetadataRequest(
        @jakarta.validation.constraints.NotBlank(message = "파일 이름은 필수입니다.")
        String fileName,
        @jakarta.validation.constraints.NotNull(message = "파일 크기는 필수입니다.")
        @jakarta.validation.constraints.Positive(message = "파일 크기는 0보다 커야 합니다.")
        Long fileSize
) {
    public static FileMetadataRequest of(String fileName,Long fileSize){
        return new FileMetadataRequest(fileName, fileSize);
    }
}
