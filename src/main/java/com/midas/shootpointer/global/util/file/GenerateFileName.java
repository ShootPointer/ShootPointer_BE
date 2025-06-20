package com.midas.shootpointer.global.util.file;

import com.midas.shootpointer.global.util.uuid.UUIDHandler;
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
    private final UUIDHandler uuidHandler;
    public String generate(FileType fileType, MultipartFile file){
        StringBuilder sb = new StringBuilder();
        String originalFileName = file.getOriginalFilename();
        String fileExtension = "";

        if (originalFileName != null && originalFileName.contains(".")) {
            fileExtension = originalFileName.substring(originalFileName.lastIndexOf('.'));
        }

        sb.append(uuidHandler.generate()) // 고유값으로 충돌 방지
                .append("_")
                .append(fileType.name().toLowerCase())
                .append(fileExtension);
        return sb.toString();
    }

}
