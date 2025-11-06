package com.midas.shootpointer.infrastructure.redis.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.mongodb.lang.Nullable;
import jakarta.validation.constraints.NotNull;
@JsonInclude(JsonInclude.Include.NON_NULL)
public record ProgressData(
        @NotNull ProgressType type,

        @NotNull String memberId,

        //레디스 작업 Id
        @NotNull String jobId,

        /*==========================
         *      원본 전송 중 활성화
         ==========================*/

        //진행률
        @Nullable double progress,

        //파일 전체 크기
        @Nullable long totalBytes,

        //현재 받은 파일 크기
        @Nullable long receivedBytes,


        /*==========================
         *      원본 전송 완료 활성화
         ==========================*/

        //파일 크기
        @Nullable long sizeBytes,

        //파일 크기 해시 값
        @Nullable String checksum,

        //파일 전송 시간
        @Nullable double durationSec,


        /*==========================
         *      하이라이트 영상 생성 중
         ==========================*/
        @Nullable String stage,

        @Nullable int currentClip,

        @Nullable int totalClips
) {
}
