
package com.midas.shootpointer.domain.highlight.helper;

import com.midas.shootpointer.domain.highlight.entity.HighlightEntity;
import com.midas.shootpointer.domain.highlight.repository.HighlightCommandRepository;
import com.midas.shootpointer.domain.highlight.repository.HighlightQueryRepository;
import com.midas.shootpointer.global.common.ErrorCode;
import com.midas.shootpointer.global.exception.CustomException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.io.TempDir;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class HighlightUtilImplTest {
    @InjectMocks
    private HighlightUtilImpl highlightUtil;

    @Mock
    private HighlightQueryRepository queryRepository;

    @Mock
    private HighlightCommandRepository commandRepository;

    @TempDir
    Path tempDir;

    @Test
    @DisplayName("존재하지 않는 디렉토리인 경우 새로 생성하고 경로를 반환합니다.")
    void getDirectoryPath() {
        //given
        highlightUtil=new HighlightUtilImpl(tempDir.toString(),queryRepository,commandRepository);
        String highlightKey=UUID.randomUUID().toString();
        Path expectedDir=tempDir.resolve(highlightKey);

        //when
        String result=highlightUtil.getDirectoryPath(highlightKey);

        //then
        assertThat(result).isEqualTo(expectedDir.toString());
        assertThat(Files.exists(expectedDir)).isTrue();
    }

    @Test
    @DisplayName("이미 존재하는 디렉토리인 경우 기존 경로를 반환합니다.")
    void getDirectoryPath_existingDirectory() throws IOException {
        //given
        highlightUtil=new HighlightUtilImpl(tempDir.toString(),queryRepository,commandRepository);
        String highlightKey=UUID.randomUUID().toString();
        Path expectedDir=Files.createDirectories(tempDir.resolve(highlightKey));

        //when
        String result=highlightUtil.getDirectoryPath(highlightKey);

        //then
        assertThat(result).isEqualTo(expectedDir.toString());
        assertThat(Files.exists(expectedDir)).isTrue();
    }

    @Test
    @DisplayName("하이라이트 Id로 하이라이트 객체를 조회합니다.")
    void findHighlightByHighlightId() {
        //given
        UUID highlightKey=UUID.randomUUID();
        UUID highlightId=UUID.randomUUID();

        HighlightEntity highlight=makeHighlight(highlightKey,highlightId);
        //when
        when(queryRepository.findByHighlightId(highlightId)).thenReturn(Optional.of(highlight));
        HighlightEntity result=highlightUtil.findHighlightByHighlightId(highlightId);

        //then
        assertThat(result).isNotNull();
        assertThat(result.getHighlightKey()).isEqualTo(highlightKey);
        assertThat(result.getHighlightId()).isEqualTo(highlightId);
    }

    @Test
    @DisplayName("하이라이트 Id로 하이라이트 객체를 조회시 존재하지 않으면 NOT_EXIST_HIGHLIGHT 예외가 발생합니다.")
    void findHighlightByHighlightId_NOT_EXIST() {
        //given
        UUID highlightId=UUID.randomUUID();

        //when
        when(queryRepository.findByHighlightId(highlightId)).thenReturn(Optional.empty());

        //then
        assertThatThrownBy(()->highlightUtil.findHighlightByHighlightId(highlightId))
                .isInstanceOf(CustomException.class)
                .hasMessage(ErrorCode.NOT_EXIST_HIGHLIGHT.getMessage());
    }

    @Test
    @DisplayName("특정 유저의 하이라이트 리스트를 조회할 때 highlightQueryRepository-fetchAllMembersHighlights의 호출을 검증합니다.")
    void fetchAllMembersHighlights(){
        //given
        UUID memberId=UUID.randomUUID();
        Pageable pageable= PageRequest.of(0,10);
        Page<HighlightEntity> mockPage=Page.empty();

        when(queryRepository.fetchAllMembersHighlights(any(UUID.class),any(Pageable.class))).thenReturn(mockPage);

        //when
        highlightUtil.fetchMembersHighlights(memberId,pageable);

        //then
        verify(queryRepository).fetchAllMembersHighlights(any(UUID.class),any(Pageable.class));
    }

    @Test
    void savedAll() {
        //given
        List<HighlightEntity> highlightEntities=List.of(
                makeHighlight(UUID.randomUUID(),UUID.randomUUID()),
                makeHighlight(UUID.randomUUID(),UUID.randomUUID())
        );

        //when
        when(commandRepository.saveAll(highlightEntities)).thenReturn(highlightEntities);
        highlightUtil.savedAll(highlightEntities);

        //then
        verify(commandRepository,times(1)).saveAll(highlightEntities);
    }

    private HighlightEntity makeHighlight(UUID highlightKey,UUID highlightId){
        return HighlightEntity.builder()
                .highlightURL("url")
                .highlightKey(highlightKey)
                .highlightId(highlightId)
                .build();
    }
}