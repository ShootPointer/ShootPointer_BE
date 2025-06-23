package com.midas.shootpointer.domain.highlight.service.command;

import com.midas.shootpointer.domain.highlight.dto.HighlightSelectRequest;
import com.midas.shootpointer.domain.highlight.dto.HighlightSelectResponse;
import com.midas.shootpointer.domain.highlight.entity.HighlightEntity;
import com.midas.shootpointer.domain.highlight.repository.HighlightCommandRepository;
import com.midas.shootpointer.domain.highlight.repository.HighlightQueryRepository;
import com.midas.shootpointer.global.util.jwt.JwtUtil;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class HighlightCommandServiceImplTest {
    @InjectMocks
    private HighlightCommandServiceImpl highlightCommandService;

    @Mock
    private JwtUtil jwtUtil;

    @Mock
    private HighlightQueryRepository highlightQueryRepository;

    @Mock
    private HighlightCommandRepository highlightCommandRepository;

    @Test
    @DisplayName("하이라이트 영상을 선택하여 is_selected column를 true로 변환합니다.")
    void selectHighlight_SUCCEESS() {
        //given
        UUID mockMemberId=UUID.randomUUID();
        UUID highlightId1=UUID.randomUUID();
        UUID highlightId2=UUID.randomUUID();

        HighlightEntity highlight1=mock(HighlightEntity.class);
        HighlightEntity highlight2=mock(HighlightEntity.class);
        HighlightSelectRequest mockRequest=mockHighlightSelectRequest(List.of(highlightId1,highlightId2));
        String mockToken="test-token";

        when(jwtUtil.getMemberId()).thenReturn(mockMemberId);
        when(highlightQueryRepository.findByHighlightId(highlightId1)).thenReturn(Optional.of(highlight1));
        when(highlightQueryRepository.findByHighlightId(highlightId2)).thenReturn(Optional.of(highlight2));

        when(highlightQueryRepository.isMembersHighlight(any(),any())).thenReturn(true);
        when(highlightQueryRepository.isMembersHighlight(any(),any())).thenReturn(true);


        //when
        HighlightSelectResponse response=highlightCommandService.selectHighlight(mockRequest,mockToken);

        //then
        verify(highlight1).select();
        verify(highlight2).select();
        verify(highlightCommandRepository).saveAll(List.of(highlight1,highlight2));

        Assertions.assertThat(response.getSelectedHighlightIds()).isEqualTo(mockRequest.getSelectedHighlightIds());
    }
    /**
     * Mock HighlightSelectRequest
     */
    private HighlightSelectRequest mockHighlightSelectRequest(List<UUID> highlightIds){
        return HighlightSelectRequest
                .builder()
                .selectedHighlightIds(highlightIds)
                .build();
    }


}