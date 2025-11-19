package com.midas.shootpointer.infrastructure.redis.helper;

import com.midas.shootpointer.domain.progress.ProgressType;
import com.midas.shootpointer.domain.progress.dto.ProgressResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
@Slf4j
public class ProgressValidator {
    public  void validate(ProgressResponse response){
        ProgressType type=response.type();

        switch (type){
            case UPLOADING :
            case PROCESSING:
                requireNotNull(response.progress(), "progress");
                break;
        }
    }

    private void requireNotNull(Object obj, String field){
        if(obj==null){
            log.error("[Redis SUB] field is null : field = {} time = {}", field, LocalDateTime.now());
            throw new IllegalArgumentException();
        }
    }
}
