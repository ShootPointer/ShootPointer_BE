package com.midas.shootpointer.adapter.in.web;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class HealthCheckController {
    @GetMapping("/health-check")
    public ResponseEntity<String> healthCheck(){
        return ResponseEntity.ok("health-check");
    }
}
