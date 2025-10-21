package com.midas.shootpointer.domain.ranking.controller;

import com.midas.shootpointer.domain.ranking.business.service.RankingService;
import com.midas.shootpointer.domain.ranking.dto.RankingResponse;
import com.midas.shootpointer.domain.ranking.dto.RankingType;
import com.midas.shootpointer.global.dto.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/rank")
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
    @GetMapping("/last-weekly")
    public ResponseEntity<ApiResponse<RankingResponse>> fetchLastWeekRank(
            @RequestParam(value = "date",required = true) @DateTimeFormat(pattern = "yyyy-MM-dd")Date date
            ) throws IOException {
        LocalDateTime revertedDate=date.toInstant()
                .atZone(ZoneId.systemDefault())
                .toLocalDateTime();
        return ResponseEntity.ok(ApiResponse.ok(rankingService.fetchLastData(RankingType.WEEKLY,revertedDate)));
    }

    /*==========================
    *
    *RankingController
    *
    * @parm date : 요청 날짜
    * @return 저번 달 랭킹 정보
    * @author kimdoyeon
    * @version 1.0.0
    * @date 25. 10. 20.
    *
    ==========================**/
    @GetMapping("/last-month")
    public ResponseEntity<ApiResponse<RankingResponse>> fetchLastMonth(
            @RequestParam(value = "date",required = true)@DateTimeFormat(pattern = "yyyy-MM-dd")Date date
    ) throws IOException {
        LocalDateTime revertedDate=date.toInstant()
                .atZone(ZoneId.systemDefault())
                .toLocalDateTime();
        return ResponseEntity.ok(ApiResponse.ok(rankingService.fetchLastData(RankingType.MONTHLY,revertedDate)));
    }

}
