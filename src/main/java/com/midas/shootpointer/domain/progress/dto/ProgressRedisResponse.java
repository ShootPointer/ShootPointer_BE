package com.midas.shootpointer.domain.progress.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.midas.shootpointer.domain.progress.ProgressType;
import com.mongodb.lang.Nullable;
import jakarta.validation.constraints.NotNull;

/**
 * OpenCv->Redis subscribe 응답 dto
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public record ProgressRedisResponse(
        @NotNull int status,

        @NotNull boolean success,

        @NotNull long timeStamp,

        @NotNull ProgressType type,

        @NotNull String jobId,

        @NotNull String memberId,
        /*=====================================
         *      원본 전송 / 하이라이트 생성 중 활성화
         =======================================*/

        //진행률
        @Nullable double progress
) {
}
