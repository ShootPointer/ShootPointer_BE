package com.midas.shootpointer.domain.highlight.mapper;

import com.midas.shootpointer.domain.highlight.dto.HighlightInfoResponse;
import com.midas.shootpointer.domain.highlight.dto.HighlightSelectResponse;
import com.midas.shootpointer.domain.highlight.entity.HighlightEntity;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class HighlightMapperImplTest {

    private final HighlightMapperImpl mapper=new HighlightMapperImpl();

    @Test
    @DisplayName("List<UUID> 형태를 HighlightSelectResponse 형태로 매핑합니다.")
    void entityToResponse_LIST_UUIDS(){
        //given
        UUID uuid1=UUID.randomUUID();
        UUID uuid2=UUID.randomUUID();
        UUID uuid3=UUID.randomUUID();

        List<UUID> uuids=List.of(uuid1,uuid2,uuid3);

        //when
        HighlightSelectResponse result=mapper.entityToResponse(uuids);

        //then
        assertThat(result.getSelectedHighlightIds().get(0)).isEqualTo(uuid1);
        assertThat(result.getSelectedHighlightIds().get(1)).isEqualTo(uuid2);
        assertThat(result.getSelectedHighlightIds().get(2)).isEqualTo(uuid3);
    }
    @Test
    @DisplayName("HighlightEntity 형태를 HighlightInfoResponse 형태로 매핑합니다.")
    void infoResponseToEntity(){
        //given
        UUID highlightId=UUID.randomUUID();
        UUID highlightKey=UUID.randomUUID();
        LocalDateTime now=LocalDateTime.now();

        HighlightEntity entity=HighlightEntity.builder()
                .highlightURL("url")
                .highlightKey(highlightKey)
                .highlightId(highlightId)
                .isSelected(true)
                .twoPointCount(20)
                .threePointCount(30)
                .build();

        entity.setCreatedAt(now);

        //when
        HighlightInfoResponse result=mapper.infoResponseToEntity(entity);

        //then
        assertThat(result.createdDate()).isEqualTo(now);
        assertThat(result.highlightId()).isEqualTo(highlightId);
        assertThat(result.totalTwoPoint()).isEqualTo(entity.totalTwoPoint());
        assertThat(result.totalThreePoint()).isEqualTo(entity.totalThreePoint());

    }

}