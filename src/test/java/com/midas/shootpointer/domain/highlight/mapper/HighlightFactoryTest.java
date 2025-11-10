package com.midas.shootpointer.domain.highlight.mapper;

import com.midas.shootpointer.domain.backnumber.entity.BackNumber;
import com.midas.shootpointer.domain.backnumber.entity.BackNumberEntity;
import com.midas.shootpointer.domain.highlight.dto.HighlightInfo;
import com.midas.shootpointer.domain.highlight.entity.HighlightEntity;
import com.midas.shootpointer.domain.member.entity.Member;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class HighlightFactoryTest {
    private HighlightFactory factory=new HighlightFactory();

    @BeforeEach
    void setUp() {
        factory = new HighlightFactory();
        try {
            var field = HighlightFactory.class.getDeclaredField("pathPrefix");
            field.setAccessible(true);
            field.set(factory, "https://cdn.example.com/videos/");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    @DisplayName("List<fileName> 형태를 List<HighlightEntity> 형태로 매핑합니다.")
    void createHighlightEntities(){
        //given
        List<HighlightInfo> highlightInfos=List.of(
                HighlightInfo.of("string1",20,30),
                HighlightInfo.of("string2",10,20),
                HighlightInfo.of("string3",0,10)
        );

        UUID highlightKey= UUID.randomUUID();
        Member member=Member.builder()
                              .email("test@naver.com")
                              .username("test")
                              .build();
        BackNumberEntity backNumber=BackNumberEntity.builder()
                .backNumber(BackNumber.of(10))
                .build();

        //when
        List<HighlightEntity> result=factory.createHighlightEntities(highlightInfos,highlightKey,member,backNumber);

        //then

        for (int i = 0; i < result.size(); i++) {
            HighlightEntity entity = result.get(i);
            HighlightInfo info = highlightInfos.get(i);

            assertThat(entity.getHighlightKey()).isEqualTo(highlightKey);
            assertThat(entity.getMember()).isEqualTo(member);
            assertThat(entity.getBackNumber()).isEqualTo(backNumber);

            assertThat(entity.getHighlightURL()).isEqualTo("https://cdn.example.com/videos/" + info.highlightUrl());

            assertThat(entity.getTwoPointCount()).isEqualTo(info.twoPointCount());
            assertThat(entity.getThreePointCount()).isEqualTo(info.threePointCount());
        }
    }
}