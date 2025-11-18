package com.midas.shootpointer.domain.highlight.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.UUID;

@AllArgsConstructor
@Getter
@NoArgsConstructor
@Builder
@Schema(description = "하이라이트 선택 요청 DTO 입니다.")
public class HighlightSelectRequest {
    /**
     * 게시물 - 3가지 중 1개 선택
     */
    @NotNull
    private UUID selectedHighlightIds;
}
