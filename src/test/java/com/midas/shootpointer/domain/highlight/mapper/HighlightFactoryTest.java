package com.midas.shootpointer.domain.highlight.mapper;

import com.midas.shootpointer.domain.highlight.entity.HighlightEntity;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class HighlightFactoryTest {
    private HighlightFactory factory=new HighlightFactory();

    @Test
    @DisplayName("List<fileName> 형태를 List<HighlightEntity> 형태로 매핑합니다.")
    void createHighlightEntities(){
        //given
        List<String> fileNames=List.of(
                "file1","file2","file3"
        );
        String highlightKey= UUID.randomUUID().toString();
        UUID key=UUID.fromString(highlightKey);

        //when
        List<HighlightEntity> result=factory.createHighlightEntities(fileNames,highlightKey);

        //then
        assertThat(result).isNotEmpty();
        assertThat(result).hasSize(3);
        assertThat(result.get(0).getHighlightKey()).isEqualTo(key);
        assertThat(result.get(1).getHighlightKey()).isEqualTo(key);
        assertThat(result.get(2).getHighlightKey()).isEqualTo(key);

        assertThat(result.get(0).getHighlightURL()).isEqualTo(fileNames.get(0));
        assertThat(result.get(1).getHighlightURL()).isEqualTo(fileNames.get(1));
        assertThat(result.get(2).getHighlightURL()).isEqualTo(fileNames.get(2));
    }
}