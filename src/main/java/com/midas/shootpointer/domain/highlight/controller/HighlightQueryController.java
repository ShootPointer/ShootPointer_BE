package com.midas.shootpointer.domain.highlight.controller;

import com.midas.shootpointer.domain.highlight.business.HighlightManager;
import com.midas.shootpointer.domain.highlight.dto.HighlightCalendarResponse;
import com.midas.shootpointer.domain.highlight.dto.HighlightInfoResponse;
import com.midas.shootpointer.domain.highlight.dto.PeriodHighlightResponse;
import com.midas.shootpointer.global.dto.ApiResponse;
import com.midas.shootpointer.global.security.SecurityUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/highlight")
public class HighlightQueryController {
    private final HighlightManager manager;

    @GetMapping("/list")
    public ResponseEntity<ApiResponse<Page<HighlightInfoResponse>>> highlightList(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ){
        UUID memberId= SecurityUtils.getCurrentMemberId();
        return ResponseEntity.ok(ApiResponse.ok( manager.listByPaging(page,size,memberId)));
    }

    /**
     * @param period WEEKLY : 이번 주 / MONTHLY : 이번 달
     * @return 이번 주 / 이번 달 인기 하이라이트 조회
     */
    @GetMapping
    public ResponseEntity<ApiResponse<List<PeriodHighlightResponse>>> periodHighlight(@RequestParam(value = "period")String period){
        return ResponseEntity.ok(ApiResponse.ok(manager.fetchAllMembersHighlights(period)));
    }

    /**
     * @param year 조회 연도
     * @param month 조회 달
     * @return 캘린더형 유저의 날짜별 하이라이트 영상 리스트 조회
     */
    @GetMapping("/calendar")
    public ResponseEntity<ApiResponse<HighlightCalendarResponse>> fetchCalendar(
            @RequestParam(value = "year") int year,
            @RequestParam(value = "month")int month
    ){
        UUID memberId=SecurityUtils.getCurrentMemberId();
        return ResponseEntity.ok(ApiResponse.ok(manager.fetchCalendar(year,month,memberId)));
    }

    @GetMapping("/latest")
    public ResponseEntity<ApiResponse<HighlightInfoResponse>> latestCreatedHighlights(
            @RequestParam(value = "jobId") UUID jobId
    ){
        UUID memberId=SecurityUtils.getCurrentMemberId();
        return ResponseEntity.ok(ApiResponse.ok(manager.fetchLatestCreatedHighlights(jobId,memberId)));
    }
}
