package com.midas.shootpointer.batch.processor.ranking;

import com.midas.shootpointer.batch.dto.HighlightWithMemberDto;
import com.midas.shootpointer.domain.ranking.entity.RankingEntry;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
public class RankingProcessor implements ItemProcessor<HighlightWithMemberDto, Map<UUID, RankingEntry>> {

    private final Map<UUID,RankingEntry> rankingEntryMap=new TreeMap<>(new Comparator<UUID>() {
        @Override
        public int compare(UUID o1, UUID o2) {
            //total score 정렬 - 내림차순
            //three score 정렬 - 내림차순
            if (Objects.equals(rankingEntryMap.get(o1).getTotalScore(), rankingEntryMap.get(o2).getTotalScore())){
                return Integer.compare(rankingEntryMap.get(o2).getThreeScore(),rankingEntryMap.get(o1).getThreeScore());
            }
            return Integer.compare(rankingEntryMap.get(o2).getTotalScore(),rankingEntryMap.get(o1).getTotalScore());
        }
    });

    private final int TWO=2;
    private final int THREE=3;

    @Override
    public Map<UUID, RankingEntry> process(HighlightWithMemberDto item) {
        /**
         * 1. 점수 계산
         */
        int twoPointTotal=item.getTwoPointCount() * TWO;
        int threePointTotal=item.getThreePointCount() * THREE;
        int totalPoint=twoPointTotal+threePointTotal;

        /**
         * 2. RankingEntry 생성
         */
        RankingEntry rankingEntry=RankingEntry.builder()
                .memberId(item.getMemberId())
                .memberName(item.getMemberName())
                .threeScore(threePointTotal)
                .twoScore(twoPointTotal)
                .totalScore(totalPoint)
                .build();
        /**
         * 3. key : 멤버Id value : entry
         */
        rankingEntryMap.put(item.getMemberId(),rankingEntry);

        /**
         * 4. totalScore 기준 정렬 내림차순
         */

        return null;
    }
}
