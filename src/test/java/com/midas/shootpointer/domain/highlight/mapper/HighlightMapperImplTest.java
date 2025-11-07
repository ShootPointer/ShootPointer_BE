package com.midas.shootpointer.domain.highlight.mapper;

import com.midas.shootpointer.domain.highlight.dto.HighlightSelectResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

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


}