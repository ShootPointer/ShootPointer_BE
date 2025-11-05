package com.midas.shootpointer.domain.highlight.mapper;

import com.midas.shootpointer.domain.highlight.entity.HighlightEntity;
import com.midas.shootpointer.domain.member.entity.Member;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

@Component
public class HighlightFactory {
    public List<HighlightEntity> createHighlightEntities(List<String> fileNames, String highlightKey, Member member){
        UUID key=UUID.fromString(highlightKey);
        return fileNames.stream()
                .map(name -> HighlightEntity.builder()
                        .member(member)
                        .highlightURL(name)
                        .highlightKey(key)
                        .build())
                .toList();
    }
}
