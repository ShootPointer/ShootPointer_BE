package com.midas.shootpointer.domain.progress.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/progress")
public class ProgressController {

   /* @GetMapping("/subscribe")
    public ResponseEntity<ApiResponse<SseEmitter>> subscribe(
            @RequestHeader(value = "Last-Event-ID",required = false,defaultValue = "") final String lastEventId
    ){
        UUID memberId= SecurityUtils.getCurrentMemberId();
       // return ResponseEntity.ok(ApiResponse.ok());
    }*/
}
