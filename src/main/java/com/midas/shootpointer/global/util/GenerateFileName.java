package com.midas.shootpointer.global.util;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

/**
 * 파일 이름을 생성 Util
 */
@Component
@RequiredArgsConstructor
public class GenerateFileName {
    private UUID userId;
    private FileType fileType;

    public static String withUserId(UUID userId, FileType fileType, MultipartFile file){
        StringBuilder sb=new StringBuilder();
        sb.append(file.getName()).append("_")
                .append(userId.toString()).append("_")
                .append(fileType).append("_");

        return sb.toString();
    }

    public static String withOutUserId(FileType fileType,MultipartFile file){
        StringBuilder sb=new StringBuilder();
        sb.append(file.getName()).append("_")
                .append(fileType);

        return sb.toString();
    }
}
