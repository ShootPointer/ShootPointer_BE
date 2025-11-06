package com.midas.shootpointer.domain.highlight.mapper;

import com.midas.shootpointer.domain.backnumber.entity.BackNumberEntity;
import com.midas.shootpointer.domain.highlight.dto.HighlightInfo;
import com.midas.shootpointer.domain.highlight.entity.HighlightEntity;
import com.midas.shootpointer.domain.member.entity.Member;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

@Component
public class HighlightFactory {
    public List<HighlightEntity> createHighlightEntities(List<HighlightInfo> highlightInfos, UUID key, Member member, BackNumberEntity backNumber){
        return highlightInfos.stream()
                .map(info -> HighlightEntity.builder()
                        .member(member)
                        .highlightURL(info.highlightUrl())
                        .highlightKey(key)
                        .twoPointCount(info.twoPointCount())
                        .threePointCount(info.threePointCount())
                        .backNumber(backNumber)
                        .build())
                .toList();
    }
}
