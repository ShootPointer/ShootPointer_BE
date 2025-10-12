package com.midas.shootpointer.infrastructure.presigned.controller;

import com.midas.shootpointer.global.dto.ApiResponse;
import com.midas.shootpointer.global.security.CustomUserDetails;
import com.midas.shootpointer.infrastructure.presigned.dto.FileMetadataRequest;
import com.midas.shootpointer.infrastructure.presigned.dto.PresignedUrlResponse;
import com.midas.shootpointer.infrastructure.presigned.service.PresignedUrlService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
@Tag(name = "영상 업로드 Pre-signed URL 발급",description = "영상 업로드를 위해 pre-signed url를 발급받는 API 입니다.")
public class PresignedUrlController {
    private final PresignedUrlService presignedUrlService;


    @Operation(
            summary = "Pre-signed URL API - [담당자 : 김도연]",
            responses = {
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200",description = "URL 발급 성공",
                            content = @Content(mediaType="application/json",
                                    schema = @Schema(implementation = com.midas.shootpointer.global.dto.ApiResponse.class))),
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "4XX",description = "URL 발급 실패",
                            content = @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = com.midas.shootpointer.global.dto.ApiResponse.class)))
            }
    )
    /*==========================
    *
    *PresignedUrlController
    *
    * @parm [request] 업로드 file Metadata
    * @return org.springframework.http.ResponseEntity<com.midas.shootpointer.global.dto.ApiResponse<com.midas.shootpointer.infrastructure.presigned.dto.PresignedUrlResponse>>
    * @author kimdoyeon
    * @version 1.0.0
    * @date 25. 10. 10.
    *
    ==========================**/
    @PostMapping("/pre-signed")
    public ResponseEntity<ApiResponse<PresignedUrlResponse>> getPreSignedUrl(@RequestBody FileMetadataRequest request, @AuthenticationPrincipal CustomUserDetails userDetails){
        UUID memberId = userDetails.getMember().getMemberId();
        return ResponseEntity.ok(ApiResponse.ok(presignedUrlService.createPresignedUrl(memberId,request)));
    }
}
