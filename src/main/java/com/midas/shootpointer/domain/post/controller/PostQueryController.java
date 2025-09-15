package com.midas.shootpointer.domain.post.controller;

import com.midas.shootpointer.domain.post.business.query.PostQueryService;
import com.midas.shootpointer.domain.post.dto.PostResponse;
import com.midas.shootpointer.global.dto.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/post")
@Tag(name = "게시판 - 조회",description = "게시물 R(READ) API")
public class PostQueryController {
    private final PostQueryService postQueryService;

    @Operation(
            summary = "게시물 등록 API - [담당자 : 김도연]",
            responses = {
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200",description = "게시물 조회 성공",
                            content = @Content(mediaType="application/json",
                                    schema = @Schema(implementation = com.midas.shootpointer.global.dto.ApiResponse.class))),
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "4XX",description = "게시물 조회 실패",
                            content = @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = com.midas.shootpointer.global.dto.ApiResponse.class)))
            }
    )
    /*==========================
    *
    *PostQueryController
    *
    * @parm postId : 게시물 id
    * @return org.springframework.http.ResponseEntity<com.midas.shootpointer.global.dto.ApiResponse<java.lang.Long>>
    * @author kimdoyeon
    * @version 1.0.0
    * @date 25. 9. 8.
    *
    ==========================**/
    @GetMapping("{postId}")
    public ResponseEntity<ApiResponse<PostResponse>> singleRead(@PathVariable(value = "postId") Long postId){
        return ResponseEntity.ok(ApiResponse.ok(postQueryService.singleRead(postId)));
    }
}
