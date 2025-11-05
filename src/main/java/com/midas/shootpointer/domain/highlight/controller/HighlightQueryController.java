package com.midas.shootpointer.domain.highlight.controller;

import com.midas.shootpointer.domain.highlight.business.HighlightManager;
import com.midas.shootpointer.domain.highlight.dto.HighlightInfoResponse;
import com.midas.shootpointer.global.dto.ApiResponse;
import com.midas.shootpointer.global.security.SecurityUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/highlight")
public class HighlightQueryController {
    private HighlightManager manager;

    @GetMapping("/list")
    public ResponseEntity<ApiResponse<Page<HighlightInfoResponse>>> highlightList(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ){
        UUID memberId= SecurityUtils.getCurrentMemberId();
        return ResponseEntity.ok(ApiResponse.ok( manager.listByPaging(page,size,memberId)));
    }
}
