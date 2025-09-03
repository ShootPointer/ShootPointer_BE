package com.midas.shootpointer.domain.like.controller;

import com.midas.shootpointer.domain.like.business.command.LikeCommandService;
import com.midas.shootpointer.domain.member.entity.Member;
import com.midas.shootpointer.global.dto.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/like")
@RequiredArgsConstructor
@Tag(name = "좋아요",description = "좋아요 등록 및 취소")
public class LikeController {
    private final LikeCommandService likeCommandService;

    @Operation(
            summary = "좋아요 요청 API - [담당자 : 김도연]",
            responses = {
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "201",description = "좋아요 등록 성공",
                            content = @Content(mediaType="application/json",
                                    schema = @Schema(implementation = com.midas.shootpointer.global.dto.ApiResponse.class))),
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "4XX",description = "좋아요 등록 실패",
                            content = @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = com.midas.shootpointer.global.dto.ApiResponse.class)))
            }
    )
    /*==========================
    *
    *LikeController
    *
    * @parm [postId] 게시물 Id
    * @return org.springframework.http.ResponseEntity<com.midas.shootpointer.global.dto.ApiResponse<java.lang.Long>>
    * @author kimdoyeon
    * @version 1.0.0
    * @date 25. 9. 2.
    *
    ==========================**/
    @PostMapping("{postId}")
    public ResponseEntity<ApiResponse<Long>> create(
            @PathVariable(value = "postId") String postId
    ) {

        Member member=new Member();
        return ResponseEntity.ok(ApiResponse.created(likeCommandService.create(Long.parseLong(postId), member)));
    }

    @Operation(
            summary = "좋아요 취소 API - [담당자 : 김도연]",
            responses = {
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200",description = "좋아요 취소 성공",
                            content = @Content(mediaType="application/json",
                                    schema = @Schema(implementation = com.midas.shootpointer.global.dto.ApiResponse.class))),
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "4XX",description = "좋아요 취소 실패",
                            content = @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = com.midas.shootpointer.global.dto.ApiResponse.class)))
            }
    )
    /*==========================
    *
    *LikeController
    *
    * @parm [postId] 게시물 Id
    * @return org.springframework.http.ResponseEntity<com.midas.shootpointer.global.dto.ApiResponse<java.lang.Long>>
    * @author kimdoyeon
    * @version 1.0.0
    * @date 25. 9. 2.
    *
    ==========================**/
    @DeleteMapping("{postId}")
    public ResponseEntity<ApiResponse<Long>> delete(
            @PathVariable(value = "postId") String postId
    ) {
        Member member=new Member();
        return ResponseEntity.ok(ApiResponse.ok(likeCommandService.delete(Long.parseLong(postId), member)));
    }
}
