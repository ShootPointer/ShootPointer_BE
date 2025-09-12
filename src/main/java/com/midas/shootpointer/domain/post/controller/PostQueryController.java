package com.midas.shootpointer.domain.post.controller;

import com.midas.shootpointer.domain.post.business.query.PostQueryService;
import com.midas.shootpointer.domain.post.dto.response.PostListResponse;
import com.midas.shootpointer.domain.post.dto.response.PostResponse;
import com.midas.shootpointer.domain.post.mapper.PostMapper;
import com.midas.shootpointer.global.dto.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/post")
@Tag(name = "게시판 - 조회",description = "게시물 R(READ) API")
public class PostQueryController {
    private final PostQueryService postQueryService;
    private final PostMapper postMapper;

    @Operation(
            summary = "게시물 단건 조회 API - [담당자 : 김도연]",
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


    @Operation(
            summary = "게시물 다건 조회 API - [담당자 : 김도연]",
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
    * @parm postId : 마지막 요청 postId / size : 요청 크기 / type : 최신순 OR 인기순 (디폴트 값은 최신순)
    * @return 여러 건의 게시물.
    * @author kimdoyeon
    * @version 1.0.0
    * @date 25. 9. 10.
    *
    ==========================**/
    @GetMapping
    public ResponseEntity<ApiResponse<PostListResponse>> multiRead(@RequestParam(required = false,defaultValue = "922337203685477580")Long postId,
                                                                   @RequestParam(required = false,defaultValue = "10")int size,
                                                                   @RequestParam(required = false,defaultValue = "latest")String type){
        return ResponseEntity.ok(ApiResponse.ok(postQueryService.multiRead(postId,size,type)));
    }
}
