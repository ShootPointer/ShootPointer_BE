package com.midas.shootpointer.global.util.file;

import com.midas.shootpointer.global.common.ErrorCode;
import com.midas.shootpointer.global.exception.CustomException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.unit.DataSize;

@Component
public class FileValidatorImpl implements FileValidator{
    @Value("${video.max-size}")
    private DataSize maxFileSize;

    @Override
    public void isValidFileSize(long size) {
        //파일 사이즈 - 바이트 단위
        long maxSize=maxFileSize.toBytes();

        if (maxSize<size){
            throw new CustomException(ErrorCode.FILE_SIZE_EXCEEDED);
        }
    }

    @Override
    public void isValidFileType(String fileType) {
        if(fileType==null || fileType.isBlank() || !fileType.endsWith(".mp4")){
            throw new CustomException(ErrorCode.INVALID_FILE_TYPE);
        }
    }
}
