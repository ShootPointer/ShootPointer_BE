package com.midas.shootpointer.domain.ranking.controller;

import com.midas.shootpointer.domain.ranking.business.service.RankingService;
import com.midas.shootpointer.domain.ranking.dto.RankingResponse;
import com.midas.shootpointer.domain.ranking.dto.RankingType;
import com.midas.shootpointer.global.dto.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/rank")
@Tag(name = "랭킹 - 득점 순위 랭킹 조회")
public class RankingController {
    private final RankingService rankingService;

    /*==========================
    *
    *RankingController
    *
    * @parm date : 요청 날짜
    * @return 저번 주 랭킹 정보
    * @author kimdoyeon
    * @version 1.0.0
    * @date 25. 10. 20.
    *
    ==========================**/
    @GetMapping("/last-week")
    public ResponseEntity<ApiResponse<RankingResponse>> fetchLastWeekRank(
            @RequestParam(value = "date",required = true) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date
            ) throws IOException {
        LocalDateTime dateTime = date.atStartOfDay();
        return ResponseEntity.ok(ApiResponse.ok(rankingService.fetchLastData(RankingType.WEEKLY,dateTime)));
    }

    /**
     * Retrieves the ranking data for the month preceding the provided date.
     *
     * The provided `date` is interpreted at the start of that day to determine the previous month for which rankings are fetched.
     *
     * @param date the reference date (ISO-8601 `yyyy-MM-dd`) used to identify the previous month
     * @return an ApiResponse wrapping the RankingResponse for the previous month
     * @throws IOException if an I/O error occurs while fetching ranking data
     */
    @GetMapping("/last-month")
    public ResponseEntity<ApiResponse<RankingResponse>> fetchLastMonth(
            @RequestParam(value = "date",required = true) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date
    ) throws IOException {
        LocalDateTime dateTime = date.atStartOfDay();
        return ResponseEntity.ok(ApiResponse.ok(rankingService.fetchLastData(RankingType.MONTHLY,dateTime)));
    }

    /**
     * Fetches ranking data for the current week.
     *
     * @return an ApiResponse containing this week's ranking information wrapped in a ResponseEntity
     */
    @GetMapping("/this-week")
    public ResponseEntity<ApiResponse<RankingResponse>> fetchThisWeek(){
        return ResponseEntity.ok(ApiResponse.ok(rankingService.fetchThisData(RankingType.WEEKLY)));
    }

    /**
     * Retrieves the current month's ranking data.
     *
     * @return an ApiResponse containing the current month's RankingResponse
     */
    @GetMapping("/this-month")
    public ResponseEntity<ApiResponse<RankingResponse>> fetchThisMonth(){
        return ResponseEntity.ok(ApiResponse.ok(rankingService.fetchThisData(RankingType.MONTHLY)));
    }

}