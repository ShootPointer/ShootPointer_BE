package com.midas.shootpointer.domain.backnumber.controller;

import com.midas.shootpointer.domain.backnumber.dto.BackNumberRequest;
import com.midas.shootpointer.domain.backnumber.dto.BackNumberResponse;
import com.midas.shootpointer.domain.backnumber.service.BackNumberService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.headers.Header;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/backNumber")
@RequiredArgsConstructor
@Tag(name = "등 번호", description = "등 번호 API")
public class BackNumberController {
    private final BackNumberService backNumberService;
    /*==========================
    *
    *BackNumberController
    *
    * @parm 등번호 request dto
    * @return ApiResponseDto
    * @author kimdoyeon
    * @version 1.0.0
    * @date 6/16/25
    *
    ==========================**/
    @Operation(
            summary = "등 번호 등록 API - [담당자 : 김도연]",
            responses = {
                    @ApiResponse(responseCode = "200",description = "등 번호 등록 성공",
                        content = @Content(mediaType="application/json",
                        schema = @Schema(implementation = com.midas.shootpointer.global.dto.ApiResponse.class))),
                    @ApiResponse(responseCode = "4XX",description = "등 번호 등록 실패",
                        content = @Content(mediaType = "application/json",
                        schema = @Schema(implementation = com.midas.shootpointer.global.dto.ApiResponse.class)))
            }
    )
    @PostMapping
    public ResponseEntity<com.midas.shootpointer.global.dto.ApiResponse<BackNumberResponse>> create(
            @RequestPart(value = "backNumberRequestDto") BackNumberRequest request,
            @RequestPart (value = "image") MultipartFile image,
            @RequestHeader("Authorization") String jwtToken
    ){
        String token = jwtToken.substring(7);
        BackNumberResponse response=backNumberService.create(token, request,image);
        return ResponseEntity.ok(com.midas.shootpointer.global.dto.ApiResponse.created(response));
    }
}
