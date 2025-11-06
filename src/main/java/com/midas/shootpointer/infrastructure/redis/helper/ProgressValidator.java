package com.midas.shootpointer.infrastructure.redis.helper;

import com.midas.shootpointer.infrastructure.redis.dto.ProgressData;
import com.midas.shootpointer.infrastructure.redis.dto.ProgressType;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
@Slf4j
public class ProgressValidator {
    public  void validate(ProgressData data){
        ProgressType type=data.type();

        switch (type){
            case UPLOADING -> {
                requireNotNull(data.progress(), "progress");
                requireNotNull(data.totalBytes(),"totalBytes");
                requireNotNull(data.receivedBytes(),"receivedBytes");
            }
            case UPLOAD_COMPLETE -> {
                requireNotNull(data.sizeBytes(),"sizeBytes");
                requireNotNull(data.checksum(),"checkSum");
                requireNotNull(data.durationSec(),"durationSec");
            }
            case PROCESSING -> {
                requireNotNull(data.stage(),"stage");
                requireNotNull(data.currentClip(),"currentClip");
                requireNotNull(data.totalClips(),"totalClips");
            }

        }
    }

    private void requireNotNull(Object obj, String field){
        if(obj==null){
            log.error("[Redis sub] field is null : field = {} time = {}", field, LocalDateTime.now());
            throw new IllegalArgumentException();
        }
    }
}
