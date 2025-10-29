package com.midas.shootpointer.domain.ranking.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.midas.shootpointer.WithMockCustomMember;
import com.midas.shootpointer.domain.ranking.business.service.RankingService;
import com.midas.shootpointer.domain.ranking.dto.RankingResponse;
import com.midas.shootpointer.domain.ranking.dto.RankingType;
import com.midas.shootpointer.domain.ranking.entity.RankingEntry;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc
@ActiveProfiles({"test","batch"})
@SpringBootTest
@WithMockCustomMember
class RankingControllerTest  {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private RankingService rankingService;

    private final String baseUrl = "/api/rank";

    @BeforeEach
    void setUp(){
        objectMapper=new ObjectMapper();
    }

    @Test
    @DisplayName("저번 주 랭킹 GET 요청 성공시 RankingResponse를 반환합니다._SUCCESS")
    void fetchLastWeekRank() throws Exception {
        //given
        List<RankingEntry> top10=List.of(
                makeRankingEntry(1,100,50,50),
                makeRankingEntry(2,80,40,40),
                makeRankingEntry(3,60,40,20),
                makeRankingEntry(4,40,20,20),
                makeRankingEntry(5,20,10,10)
        );
        RankingResponse expectedResponse=RankingResponse.builder()
                .rankingList(top10)
                .rankingType(RankingType.WEEKLY)
                .build();


        //when
        when(rankingService.fetchLastData(eq(RankingType.WEEKLY),any(LocalDateTime.class))).thenReturn(expectedResponse);

        //then
        mockMvc.perform(get(baseUrl+"/last-week")
                .param("date","2025-10-22"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("OK"))
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.rankingType").value("WEEKLY"))

                .andExpect(jsonPath("$.data.rankingList[0].rank").value(1))
                .andExpect(jsonPath("$.data.rankingList[0].totalScore").value(100))
                .andExpect(jsonPath("$.data.rankingList[0].twoScore").value(50))
                .andExpect(jsonPath("$.data.rankingList[0].threeScore").value(50))

                .andExpect(jsonPath("$.data.rankingList[1].rank").value(2))
                .andExpect(jsonPath("$.data.rankingList[1].totalScore").value(80))
                .andExpect(jsonPath("$.data.rankingList[1].twoScore").value(40))
                .andExpect(jsonPath("$.data.rankingList[1].threeScore").value(40))

                .andExpect(jsonPath("$.data.rankingList[2].rank").value(3))
                .andExpect(jsonPath("$.data.rankingList[2].totalScore").value(60))
                .andExpect(jsonPath("$.data.rankingList[2].twoScore").value(40))
                .andExpect(jsonPath("$.data.rankingList[2].threeScore").value(20))

                .andExpect(jsonPath("$.data.rankingList[3].rank").value(4))
                .andExpect(jsonPath("$.data.rankingList[3].totalScore").value(40))
                .andExpect(jsonPath("$.data.rankingList[3].twoScore").value(20))
                .andExpect(jsonPath("$.data.rankingList[3].threeScore").value(20))

                .andExpect(jsonPath("$.data.rankingList[4].rank").value(5))
                .andExpect(jsonPath("$.data.rankingList[4].totalScore").value(20))
                .andExpect(jsonPath("$.data.rankingList[4].twoScore").value(10))
                .andExpect(jsonPath("$.data.rankingList[4].threeScore").value(10))
                .andDo(print());
    }

    @Test
    @DisplayName("저번 달 랭킹 GET 요청 성공시 RankingResponse를 반환합니다._SUCCESS")
    void fetchLastMonth() throws Exception {
        //given
        List<RankingEntry> top10=List.of(
                makeRankingEntry(1,100,50,50),
                makeRankingEntry(2,80,40,40),
                makeRankingEntry(3,60,40,20),
                makeRankingEntry(4,40,20,20),
                makeRankingEntry(5,20,10,10)
        );
        RankingResponse expectedResponse=RankingResponse.builder()
                .rankingList(top10)
                .rankingType(RankingType.MONTHLY)
                .build();


        //when
        when(rankingService.fetchLastData(eq(RankingType.MONTHLY),any(LocalDateTime.class))).thenReturn(expectedResponse);

        //then
        mockMvc.perform(get(baseUrl+"/last-month")
                        .param("date","2025-10-22"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("OK"))
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.rankingType").value("MONTHLY"))

                .andExpect(jsonPath("$.data.rankingList[0].rank").value(1))
                .andExpect(jsonPath("$.data.rankingList[0].totalScore").value(100))
                .andExpect(jsonPath("$.data.rankingList[0].twoScore").value(50))
                .andExpect(jsonPath("$.data.rankingList[0].threeScore").value(50))

                .andExpect(jsonPath("$.data.rankingList[1].rank").value(2))
                .andExpect(jsonPath("$.data.rankingList[1].totalScore").value(80))
                .andExpect(jsonPath("$.data.rankingList[1].twoScore").value(40))
                .andExpect(jsonPath("$.data.rankingList[1].threeScore").value(40))

                .andExpect(jsonPath("$.data.rankingList[2].rank").value(3))
                .andExpect(jsonPath("$.data.rankingList[2].totalScore").value(60))
                .andExpect(jsonPath("$.data.rankingList[2].twoScore").value(40))
                .andExpect(jsonPath("$.data.rankingList[2].threeScore").value(20))

                .andExpect(jsonPath("$.data.rankingList[3].rank").value(4))
                .andExpect(jsonPath("$.data.rankingList[3].totalScore").value(40))
                .andExpect(jsonPath("$.data.rankingList[3].twoScore").value(20))
                .andExpect(jsonPath("$.data.rankingList[3].threeScore").value(20))

                .andExpect(jsonPath("$.data.rankingList[4].rank").value(5))
                .andExpect(jsonPath("$.data.rankingList[4].totalScore").value(20))
                .andExpect(jsonPath("$.data.rankingList[4].twoScore").value(10))
                .andExpect(jsonPath("$.data.rankingList[4].threeScore").value(10))
                .andDo(print());

    }

    @Test
    @DisplayName("이번 주 랭킹 GET 요청 성공시 RankingResponse를 반환합니다._SUCCESS")
    void fetchThisWeekRank() throws Exception {
        //given
        List<RankingEntry> top10=List.of(
                makeRankingEntry(1,100,50,50),
                makeRankingEntry(2,80,40,40),
                makeRankingEntry(3,60,40,20),
                makeRankingEntry(4,40,20,20),
                makeRankingEntry(5,20,10,10)
        );
        RankingResponse expectedResponse=RankingResponse.builder()
                .rankingList(top10)
                .rankingType(RankingType.WEEKLY)
                .build();


        //when
        when(rankingService.fetchThisData(eq(RankingType.WEEKLY))).thenReturn(expectedResponse);

        //then
        mockMvc.perform(get(baseUrl+"/this-week"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("OK"))
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.rankingType").value("WEEKLY"))

                .andExpect(jsonPath("$.data.rankingList[0].rank").value(1))
                .andExpect(jsonPath("$.data.rankingList[0].totalScore").value(100))
                .andExpect(jsonPath("$.data.rankingList[0].twoScore").value(50))
                .andExpect(jsonPath("$.data.rankingList[0].threeScore").value(50))

                .andExpect(jsonPath("$.data.rankingList[1].rank").value(2))
                .andExpect(jsonPath("$.data.rankingList[1].totalScore").value(80))
                .andExpect(jsonPath("$.data.rankingList[1].twoScore").value(40))
                .andExpect(jsonPath("$.data.rankingList[1].threeScore").value(40))

                .andExpect(jsonPath("$.data.rankingList[2].rank").value(3))
                .andExpect(jsonPath("$.data.rankingList[2].totalScore").value(60))
                .andExpect(jsonPath("$.data.rankingList[2].twoScore").value(40))
                .andExpect(jsonPath("$.data.rankingList[2].threeScore").value(20))

                .andExpect(jsonPath("$.data.rankingList[3].rank").value(4))
                .andExpect(jsonPath("$.data.rankingList[3].totalScore").value(40))
                .andExpect(jsonPath("$.data.rankingList[3].twoScore").value(20))
                .andExpect(jsonPath("$.data.rankingList[3].threeScore").value(20))

                .andExpect(jsonPath("$.data.rankingList[4].rank").value(5))
                .andExpect(jsonPath("$.data.rankingList[4].totalScore").value(20))
                .andExpect(jsonPath("$.data.rankingList[4].twoScore").value(10))
                .andExpect(jsonPath("$.data.rankingList[4].threeScore").value(10))
                .andDo(print());
    }



    @Test
    @DisplayName("이번 달 랭킹 GET 요청 성공시 RankingResponse를 반환합니다._SUCCESS")
    void fetchThisMonthRank() throws Exception {
        //given
        List<RankingEntry> top10=List.of(
                makeRankingEntry(1,100,50,50),
                makeRankingEntry(2,80,40,40),
                makeRankingEntry(3,60,40,20),
                makeRankingEntry(4,40,20,20),
                makeRankingEntry(5,20,10,10)
        );
        RankingResponse expectedResponse=RankingResponse.builder()
                .rankingList(top10)
                .rankingType(RankingType.MONTHLY)
                .build();


        //when
        when(rankingService.fetchThisData(eq(RankingType.MONTHLY))).thenReturn(expectedResponse);

        //then
        mockMvc.perform(get(baseUrl+"/this-month"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("OK"))
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.rankingType").value("MONTHLY"))

                .andExpect(jsonPath("$.data.rankingList[0].rank").value(1))
                .andExpect(jsonPath("$.data.rankingList[0].totalScore").value(100))
                .andExpect(jsonPath("$.data.rankingList[0].twoScore").value(50))
                .andExpect(jsonPath("$.data.rankingList[0].threeScore").value(50))

                .andExpect(jsonPath("$.data.rankingList[1].rank").value(2))
                .andExpect(jsonPath("$.data.rankingList[1].totalScore").value(80))
                .andExpect(jsonPath("$.data.rankingList[1].twoScore").value(40))
                .andExpect(jsonPath("$.data.rankingList[1].threeScore").value(40))

                .andExpect(jsonPath("$.data.rankingList[2].rank").value(3))
                .andExpect(jsonPath("$.data.rankingList[2].totalScore").value(60))
                .andExpect(jsonPath("$.data.rankingList[2].twoScore").value(40))
                .andExpect(jsonPath("$.data.rankingList[2].threeScore").value(20))

                .andExpect(jsonPath("$.data.rankingList[3].rank").value(4))
                .andExpect(jsonPath("$.data.rankingList[3].totalScore").value(40))
                .andExpect(jsonPath("$.data.rankingList[3].twoScore").value(20))
                .andExpect(jsonPath("$.data.rankingList[3].threeScore").value(20))

                .andExpect(jsonPath("$.data.rankingList[4].rank").value(5))
                .andExpect(jsonPath("$.data.rankingList[4].totalScore").value(20))
                .andExpect(jsonPath("$.data.rankingList[4].twoScore").value(10))
                .andExpect(jsonPath("$.data.rankingList[4].threeScore").value(10))
                .andDo(print());
    }
    private RankingEntry makeRankingEntry(int rank,int total,int two,int three){
        return RankingEntry.builder()
                .totalScore(total)
                .twoScore(two)
                .threeScore(three)
                .rank(rank)
                .memberName("test"+rank)
                .memberId(UUID.randomUUID())
                .build();
    }
}