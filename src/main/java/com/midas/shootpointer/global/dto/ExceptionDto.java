package com.midas.shootpointer.global.dto;

import com.midas.shootpointer.global.common.ErrorCode;
import lombok.Getter;
import org.antlr.v4.runtime.misc.NotNull;

@Getter
public class ExceptionDto {
    @NotNull
    private final Integer code;

    @NotNull
    private final String message;

    public ExceptionDto(ErrorCode errorCode){
        this.code=errorCode.getCode();
        this.message=errorCode.getMessage();
    }
    public static ExceptionDto of(ErrorCode errorCode){
        return new ExceptionDto(errorCode);
    }
}