package com.midas.shootpointer.infrastructure.openCV.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class OpenCVResponse {
    @NotBlank
    private Integer status;

    @NotBlank
    private Boolean success;

    //등 번호
    private Integer backNumber;

    //정확도
    private Double confidence;

    //추출 등번호 값
    private Integer expectedNumber;

    //일치 여부
    private Boolean match;

    //오류 시 메세지 내용 (성공 시 Null)
    private String message;

}
