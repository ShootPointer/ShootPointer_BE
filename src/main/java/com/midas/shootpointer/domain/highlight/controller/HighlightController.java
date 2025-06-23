package com.midas.shootpointer.domain.highlight.controller;

import com.midas.shootpointer.domain.highlight.dto.HighlightResponse;
import com.midas.shootpointer.domain.highlight.dto.HighlightSelectRequest;
import com.midas.shootpointer.global.dto.ApiResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/highlight")
public class HighlightController {
    @PostMapping("/select")
    public ResponseEntity<ApiResponse<HighlightResponse>> selectHighlight(
            @RequestHeader("Authorization")String header,
            @RequestBody HighlightSelectRequest request
            ){

        return null;
    }
}
