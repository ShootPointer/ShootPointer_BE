package com.midas.shootpointer.domain.ranking.controller;

import com.midas.shootpointer.domain.ranking.business.service.RankingService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/rank")
public class RankingController {
    private final RankingService rankingService;


}
