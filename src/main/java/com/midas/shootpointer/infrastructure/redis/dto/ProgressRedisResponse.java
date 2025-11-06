package com.midas.shootpointer.infrastructure.redis.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.mongodb.lang.Nullable;
import jakarta.validation.constraints.NotNull;

/**
 * OpenCv->Redis subscribe 응답 dto
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public record ProgressRedisResponse(
        @NotNull int status,

        @NotNull boolean success,

        @Nullable ProgressData data,

        @NotNull String message,

        @NotNull long timeStamp
) {
}
