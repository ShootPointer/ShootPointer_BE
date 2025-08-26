package com.midas.shootpointer.domain.highlight.controller;

import com.midas.shootpointer.domain.highlight.dto.HighlightResponse;
import com.midas.shootpointer.domain.highlight.dto.HighlightSelectRequest;
import com.midas.shootpointer.domain.highlight.dto.HighlightSelectResponse;
import com.midas.shootpointer.domain.highlight.dto.UploadHighlight;
import com.midas.shootpointer.domain.highlight.service.command.HighlightCommandService;
import com.midas.shootpointer.global.dto.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/highlight")
@RequiredArgsConstructor
@Tag(name="하이라이트 영상",description = "하이라이트 CRUD 및 선택 API")
public class HighlightController {
    private final HighlightCommandService highlightCommandService;

    /*==========================
    *
    *HighlightController
    *
    * @parm header : JWT  request : 하이라이트 선택 Request
    * @return ApiResponseDto
    * @author kimdoyeon
    * @version 1.0.0
    * @date 25. 8. 26.
    *
    ==========================**/
    @Operation(
            summary = "하이라이트 영상 선택 API - [담당자 : 김도연]",
            responses = {
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200",description = "하이라이트 선택 성공",
                            content = @Content(mediaType="application/json",
                                    schema = @Schema(implementation = com.midas.shootpointer.global.dto.ApiResponse.class))),
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "4XX",description = "하이라이트 선택 실패",
                            content = @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = com.midas.shootpointer.global.dto.ApiResponse.class)))
            }
    )
    @PostMapping("/select")
    public ResponseEntity<ApiResponse<HighlightSelectResponse>> selectHighlight(
            @RequestHeader("Authorization") String header,
            @RequestBody HighlightSelectRequest request
    ) {
        return ResponseEntity.ok(ApiResponse.ok(highlightCommandService.selectHighlight(request, header)));
    }

    /*==========================
    *
    *HighlightController
    *
    * @parm
    *  token : JWT
    *  memberId : 유저Id
    *  request : 고유 하이라이트 키 값 + 생성 날짜
    *  highlights : 하이라이트 mp4 리스트
    *
    * @return
    * @author kimdoyeon
    * @version 1.0.0
    * @date 25. 8. 26.
    *
    ==========================**/
    @Operation(
            summary = "하이라이트 영상 업로드 API (openCV에서만 사용.)- [담당자 : 김도연]",
            responses = {
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200",description = "하이라이트 영상 전송 성공",
                            content = @Content(mediaType="application/json",
                                    schema = @Schema(implementation = com.midas.shootpointer.global.dto.ApiResponse.class))),
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "4XX",description = "하이라이트 영상 전송 실패",
                            content = @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = com.midas.shootpointer.global.dto.ApiResponse.class)))
            }
    )
    @PostMapping("/upload-result")
    public ResponseEntity<ApiResponse<List<HighlightResponse>>> uploadHighlights(
            @RequestHeader("X-Member-Id") String memberId,
            @RequestHeader("Authorization") String token,
            @RequestPart(value = "uploadHighlightDto") UploadHighlight request,
            @RequestPart(value = "highlights") List<MultipartFile> highlights
    ) {
        return ResponseEntity.ok(ApiResponse.ok(highlightCommandService.uploadHighlights(memberId,token,request,highlights)));
    }
}
