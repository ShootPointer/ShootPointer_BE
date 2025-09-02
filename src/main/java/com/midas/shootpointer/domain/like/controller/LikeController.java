package com.midas.shootpointer.domain.like.controller;

import com.midas.shootpointer.domain.like.business.command.LikeCommandService;
import com.midas.shootpointer.domain.member.entity.Member;
import com.midas.shootpointer.global.dto.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/like")
@RequiredArgsConstructor
public class LikeController {
    private final LikeCommandService likeCommandService;

    @PostMapping("{postId}")
    public ResponseEntity<ApiResponse<Long>> create(
            @PathVariable(value = "postId") String postId
    ) {

        Member member=new Member();
        return ResponseEntity.ok(ApiResponse.created(likeCommandService.create(Long.parseLong(postId), member)));
    }

    @DeleteMapping("{postId}")
    public ResponseEntity<ApiResponse<Long>> delete(
            @PathVariable(value = "postId") String postId
    ) {
        Member member=new Member();
        return ResponseEntity.ok(ApiResponse.ok(likeCommandService.delete(Long.parseLong(postId), member)));
    }
}
