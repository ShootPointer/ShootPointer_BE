package com.midas.shootpointer.infrastructure.openCV.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class OpenCVResponse {
    private Integer status;

    private Boolean success;

    //등 번호
    private Integer backNumber;

    //정확도
    private Double confidence;

    //추출 등번호 값
    private Integer expectedNumber;

    //일치 여부
    private Boolean match;

}
