package com.midas.shootpointer.infrastructure.openCV.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Getter
public class OpenCVResponse<T> {
    private boolean success;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private List<T> data;

    @JsonIgnore
    private HttpStatus status;
}
