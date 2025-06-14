package com.midas.shootpointer.domain.backnumber.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;


@AllArgsConstructor(access = AccessLevel.PRIVATE)
/*
 * 등번호 등록 반환 dto
 */
public class BackNumberResponse {
    @NotBlank(message = "등 번호는 공백 또는 Null일 수 없습니다.")
    @Min(1)
    @Max(9999)
    private Integer backNumber;

    public static BackNumberResponse of(Integer backNumber){
        return new BackNumberResponse(backNumber);
    }
}
