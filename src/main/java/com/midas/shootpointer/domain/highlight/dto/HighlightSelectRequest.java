package com.midas.shootpointer.domain.highlight.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.UUID;

@AllArgsConstructor
@Getter
@NoArgsConstructor
@Builder
@Schema(description = "하이라이트 선택 요청 DTO 입니다.")
public class HighlightSelectRequest {
    /**
     * 3가지 중 2개 선택
     */
    @NotEmpty
    @Size(min = 2,max = 2,message = "하이라이트를 정확히 2개를 선택해주세요.")
    private List<UUID> selectedHighlightIds;
}
