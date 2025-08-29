package com.midas.shootpointer.domain.post.controller;

import com.midas.shootpointer.domain.member.entity.Member;
import com.midas.shootpointer.domain.post.dto.PostRequest;
import com.midas.shootpointer.domain.post.business.command.PostCommandService;
import com.midas.shootpointer.global.dto.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/post")
@Tag(name = "게시판 - 등록,삭제,업데이트",description = "게시물 CUD API")
public class PostCommandController {
    private final PostCommandService postCommandService;

    /*==========================
    *
    *PostCommandController
    *
    * @parm request : 게시물 생성 dto
    * @return ApiResponseDto
    * @author kimdoyeon
    * @version 1.0.0
    * @date 25. 8. 27.
    *
    ==========================**/
    @Operation(
            summary = "게시물 등록 API - [담당자 : 김도연]",
            responses = {
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "201",description = "게시물 등록 성공",
                            content = @Content(mediaType="application/json",
                                    schema = @Schema(implementation = com.midas.shootpointer.global.dto.ApiResponse.class))),
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "4XX",description = "게시물 등록 실패",
                            content = @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = com.midas.shootpointer.global.dto.ApiResponse.class)))
            }
    )

    /*==========================
    *
    *PostCommandController
    *
    * @parm 게시물 request dto
    * @return org.springframework.http.ResponseEntity<com.midas.shootpointer.global.dto.ApiResponse<java.lang.Long>>
    * @author kimdoyeon
    * @version 1.0.0
    * @date 25. 8. 29.
    *
    ==========================**/
    @PostMapping
    public ResponseEntity<ApiResponse<Long>> create(
            @RequestBody PostRequest request
    ) {
        //TODO: member -> 현재 로그인한 멤버 가져오기.
        Member member=Member.builder()
                .memberId(UUID.randomUUID())
                .email("test@naver.com")
                .username("test")
                .build();;
        return ResponseEntity.ok(ApiResponse.created(postCommandService.create(request,member)));
    }

    @Operation(
            summary = "게시물 수정 API - [담당자 : 김도연]",
            responses = {
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200",description = "게시물 등록 성공",
                            content = @Content(mediaType="application/json",
                                    schema = @Schema(implementation = com.midas.shootpointer.global.dto.ApiResponse.class))),
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "4XX",description = "게시물 등록 실패",
                            content = @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = com.midas.shootpointer.global.dto.ApiResponse.class)))
            }
    )
    @PutMapping
    public ResponseEntity<ApiResponse<Long>> update(
          @RequestBody PostRequest request,
          @RequestParam("postId") Long postId
    ){
        //TODO: member -> 현재 로그인한 멤버 가져오기.
        Member member=Member.builder()
                .memberId(UUID.randomUUID())
                .email("test@naver.com")
                .username("test")
                .build();;
        return ResponseEntity.ok(ApiResponse.ok(postCommandService.update(request,member,postId)));
    }

}
