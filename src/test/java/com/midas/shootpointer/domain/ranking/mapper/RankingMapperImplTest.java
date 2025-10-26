package com.midas.shootpointer.domain.ranking.mapper;

import com.midas.shootpointer.batch.dto.HighlightWithMemberDto;
import com.midas.shootpointer.domain.ranking.dto.RankingResponse;
import com.midas.shootpointer.domain.ranking.dto.RankingResult;
import com.midas.shootpointer.domain.ranking.dto.RankingType;
import com.midas.shootpointer.domain.ranking.entity.RankingDocument;
import com.midas.shootpointer.domain.ranking.entity.RankingEntry;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class RankingMapperImplTest {
    private RankingMapperImpl rankingMapper = new RankingMapperImpl();

    @Test
    @DisplayName("HighlightWithMemberDto를 RankingEntry 형태로 변환합니다.")
    void dtoToEntity() {
        //given
        UUID memberId = UUID.randomUUID();
        HighlightWithMemberDto highlightWithMemberDto = HighlightWithMemberDto.builder()
                .memberId(memberId)
                .memberName("test")
                .threePointTotal(30)
                .twoPointTotal(20)
                .totalScore(50)
                .build();

        //when
        RankingEntry entry = rankingMapper.dtoToEntity(highlightWithMemberDto);

        //then
        assertThat(entry.getMemberId()).isEqualTo(memberId);
        assertThat(entry.getTwoScore()).isEqualTo(20);
        assertThat(entry.getThreeScore()).isEqualTo(30);
        assertThat(entry.getTotalScore()).isEqualTo(50);
        assertThat(entry.getMemberName()).isEqualTo("test");
    }

    @Test
    @DisplayName("RankingDocument를 RankingResponse 형태로 변환합니다.")
    void docToResponse() {
        //given
        LocalDateTime periodBegin = LocalDateTime.now();
        RankingType type = RankingType.DAILY;
        List<RankingEntry> top10 = new ArrayList<>();
        for (int idx = 1; idx <= 10; idx++) {
            top10.add(
                    RankingEntry.builder()
                            .memberName("test" + idx)
                            .threeScore(idx * 3)
                            .twoScore(idx * 2)
                            .totalScore(idx * 3 + idx * 2)
                            .memberId(UUID.randomUUID())
                            .rank(10 - idx + 1)
                            .build()
            );
        }
        RankingDocument document = RankingDocument.of(top10, periodBegin, type);

        //when
        RankingResponse result = rankingMapper.docToResponse(document);

        //then
        assertThat(result.getRankingType()).isEqualTo(RankingType.DAILY);
        assertThat(result.getRankingList()).isEqualTo(top10);
    }

    @Test
    @DisplayName("List<RankingResult>와 RankingEntry를 RankingResponse 형태로 변환합니다.")
    void resultToResponse() {
        //given
        List<RankingResult> results = new ArrayList<>();
        for (int i = 1; i <= 10; i++) {
            results.add(new RankingResult(
                    "test" + i,
                    UUID.randomUUID(),
                    i * 2 + i * 3,
                    i * 2,
                    i * 3)
            );
        }

        //when
        RankingResponse result=rankingMapper.resultToResponse(results,RankingType.DAILY);

        //then
        assertThat(result).isNotNull();
        assertThat(result.getRankingType()).isEqualTo(RankingType.DAILY);

        List<RankingEntry> entries=result.getRankingList();
        assertThat(entries).isNotEmpty();

        for (int i=0;i<10;i++){
            assertThat(results.get(i).memberId()).isEqualTo(entries.get(i).getMemberId());
            assertThat(results.get(i).memberName()).isEqualTo(entries.get(i).getMemberName());
        }
    }
}