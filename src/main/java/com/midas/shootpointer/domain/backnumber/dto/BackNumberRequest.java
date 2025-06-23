package com.midas.shootpointer.domain.backnumber.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.*;
import org.springframework.web.multipart.MultipartFile;


@AllArgsConstructor
@Getter
@NoArgsConstructor
/*
 * 등번호 등록  dto
 */
public class BackNumberRequest {
    @NotNull(message = "등 번호는 공백 또는 Null일 수 없습니다.")
    @Min(value = 1, message = "등 번호는 1 이상이어야 합니다.")
    @Max(value = 9999, message = "등 번호는 9999 이하여야 합니다.")
    @Getter
    private Integer backNumber;

    public static BackNumberRequest of(Integer backNumber){
        return new BackNumberRequest(backNumber);
    }
}
