package com.midas.shootpointer.global.exception;

import com.midas.shootpointer.global.common.ErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class CustomException extends RuntimeException{
    private final ErrorCode errorCode;

    public String getMessage(){
        return errorCode.getMessage();
    }

    public CustomException(String message) {
        super(message);
        this.errorCode = null;
    }
}
