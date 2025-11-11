package com.midas.shootpointer.domain.highlight.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Getter
@Builder
@AllArgsConstructor

public class HighlightRequest {
    //하이라이트 고유 식별 값
    @NotBlank(message = "하이라이트 식별 값은 필수입니다.")
    private UUID highlightIdentifier;

    //하이라이트 URL
    @NotBlank(message = "하이라이트 영상 URL은 필수입니다.")
    private List<HighlightInfo> highlightUrls;


    //하이라이트 생성 날짜
    @NotBlank(message = "하이라이트 생성 날짜는 필수입니다.")
    private LocalDateTime createdAt;

}
