package com.midas.shootpointer.domain.post.controller;

import com.midas.shootpointer.domain.post.business.query.PostQueryService;
import com.midas.shootpointer.domain.post.dto.response.PostListResponse;
import com.midas.shootpointer.domain.post.dto.response.PostResponse;
import com.midas.shootpointer.domain.post.dto.response.PostSort;
import com.midas.shootpointer.domain.post.dto.response.SearchAutoCompleteResponse;
import com.midas.shootpointer.global.dto.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequiredArgsConstructor
@RequestMapping("/api/post")
@Tag(name = "게시판 - 조회",description = "게시물 R(READ) API")
public class PostQueryController {
    private final PostQueryService postQueryService;

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


    @Operation(
            summary = "게시물 검색 조회 API - [담당자 : 김도연]",
            responses = {
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200",description = "게시물 검색 조회 성공",
                            content = @Content(mediaType="application/json",
                                    schema = @Schema(implementation = com.midas.shootpointer.global.dto.ApiResponse.class))),
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "4XX",description = "게시물 검색 조회 실패",
                            content = @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = com.midas.shootpointer.global.dto.ApiResponse.class)))
            }
    )
    /*==========================
    *
    *PostQueryController
    *
    * @parm [search] : 검색명
    * @return org.springframework.http.ResponseEntity<com.midas.shootpointer.global.dto.ApiResponse<com.midas.shootpointer.domain.post.dto.response.PostListResponse>>
    * @author kimdoyeon
    * @version 1.0.0
    * @date 25. 9. 18.
    *
    ==========================**/
    @GetMapping("/list")
    public ResponseEntity<ApiResponse<PostListResponse>> search(@RequestParam(required = true) String search,
                                                                @RequestParam(required = false,defaultValue = "922337203685477580") Long postId,
                                                                @RequestParam(required = false,defaultValue = "10")int size){
        return ResponseEntity.ok(ApiResponse.ok(postQueryService.search(search,postId,size)));
    }

    @Operation(
            summary = "게시물 검색 조회 API(by ElasticSearch) - [담당자 : 김도연]",
            responses = {
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200",description = "게시물 검색 조회 성공",
                            content = @Content(mediaType="application/json",
                                    schema = @Schema(implementation = com.midas.shootpointer.global.dto.ApiResponse.class))),
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "4XX",description = "게시물 검색 조회 실패",
                            content = @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = com.midas.shootpointer.global.dto.ApiResponse.class)))
            }
    )
    /*==========================
    *
    *PostQueryController
    * ElasticSearch를 이용한 게시물 검색.
    * @parm [search, postId, size] search : 검색명 / postId : 마지막 요청 게시물 id / size : 요청 크기
    * @return org.springframework.http.ResponseEntity<com.midas.shootpointer.global.dto.ApiResponse<com.midas.shootpointer.domain.post.dto.response.PostListResponse>>
    * @author kimdoyeon
    * @version 1.0.0
    * @date 25. 9. 23.
    *
    ==========================**/
    @GetMapping("/list-elastic")
    public ResponseEntity<ApiResponse<PostListResponse>> searchElastic(@RequestParam(required = false) String search,
                                                                       @RequestParam(required = false,defaultValue = "922337203685477580") Long postId,
                                                                       @RequestParam(required = false,defaultValue = "10")int size,
                                                                       @RequestParam(required = false,defaultValue = "922337203685477580")float _score,
                                                                       @RequestParam(required = false,defaultValue = "922337203685477580")Long likeCnt){
        /**
         * 검색어가 없는 경우 -> 최신 게시물 리스트 조회
         */
        if(search.isBlank()){
            return ResponseEntity.ok(ApiResponse.ok(postQueryService.multiRead(postId,size,"latest")));
        }
        PostSort sort=new PostSort(_score,likeCnt,postId);
        return ResponseEntity.ok(ApiResponse.ok(postQueryService.searchByElastic(search,size,sort)));
    }

    @GetMapping("/suggest")
    public ResponseEntity<ApiResponse<List<SearchAutoCompleteResponse>>> searchSuggest(@RequestParam(value = "keyword",required = true) String keyword){
        return ResponseEntity.ok(ApiResponse.ok(postQueryService.suggest(keyword)));
    }

}
