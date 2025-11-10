package com.midas.shootpointer.domain.progress.controller;

import com.midas.shootpointer.domain.progress.service.ProgressSseEmitter;
import com.midas.shootpointer.global.security.SecurityUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/progress")
public class ProgressController {
    private final ProgressSseEmitter progressSseEmitter;

    @GetMapping("/subscribe")
    public SseEmitter subscribe(
            @RequestHeader(value = "Last-Event-ID", required = false, defaultValue = "") final String lastEventId,
            @RequestParam String jobId
    ) {
        UUID memberId = SecurityUtils.getCurrentMemberId();
        return progressSseEmitter.createEmitter(memberId.toString(),lastEventId,jobId);
    }
}
