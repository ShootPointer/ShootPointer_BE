package com.midas.shootpointer.domain.highlight.dto;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class UploadHighlight {
    @NotEmpty(message = "UUID는 필수값입니다.")
    @Pattern(regexp = "^[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[1-5][0-9a-fA-F]{3}-[89abAB][0-9a-fA-F]{3}-[0-9a-fA-F]{12}$",
            message = "UUID 형식이 아닙니다.")
    private String highlightKey;

    @PastOrPresent(message = "생성일자는 과거 또는 현재여야 합니다.")
    private LocalDateTime createAt;
}
