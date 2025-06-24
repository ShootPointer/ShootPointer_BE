package com.midas.shootpointer.domain.highlight.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Builder
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class HighlightResponse {
    //하이라이트 Id
    @NotBlank(message = "하이라이트 Id값은 필수입니다.")
    private UUID highlightId;

    //하이라이트 고유 식별 값
    @NotBlank(message = "하이라이트 식별 값은 필수입니다.")
    private UUID highlightIdentifier;

    //하이라이트 URL
    @NotBlank(message = "하이라이트 영상 URL은 필수입니다.")
    private String highlightUrl;

    //하이라이트 파일 이름
    private String highlightName;

    //하이라이트 생성 날짜
    private LocalDateTime createdAt;

}
