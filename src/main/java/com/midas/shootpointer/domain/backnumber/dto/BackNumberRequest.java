package com.midas.shootpointer.domain.backnumber.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.*;


@AllArgsConstructor(access = AccessLevel.PRIVATE)
/*
 * 등번호 등록  dto
 */
public class BackNumberRequest {
    @NotBlank(message = "등 번호는 공백 또는 Null일 수 없습니다.")
    @Min(1)
    @Max(9999)
    @Getter
    private Integer backNumber;

    public static BackNumberRequest of(Integer backNumber){
        return new BackNumberRequest(backNumber);
    }
}
