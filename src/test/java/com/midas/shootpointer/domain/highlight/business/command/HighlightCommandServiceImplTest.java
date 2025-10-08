package com.midas.shootpointer.domain.highlight.business.command;

import com.midas.shootpointer.domain.highlight.business.HighlightManager;
import com.midas.shootpointer.domain.highlight.dto.HighlightSelectRequest;
import com.midas.shootpointer.domain.highlight.dto.UploadHighlight;
import com.midas.shootpointer.domain.member.entity.Member;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class HighlightCommandServiceImplTest {
    @InjectMocks
    private HighlightCommandServiceImpl commandService;

    @Mock
    private HighlightManager highlightManager;

    @DisplayName("manager.selectHighlight(HighlightSelectRequest, Member)가 실행되는지 검증합니다.")
    @Test
    void selectHighlight(){
        //given
        HighlightSelectRequest request=HighlightSelectRequest.builder()
                .selectedHighlightIds(List.of(UUID.randomUUID()))
                .build();
        Member member=Member.builder()
                .memberId(UUID.randomUUID())
                .username("test")
                .email("test@naver.com")
                .build();

        //when
        commandService.selectHighlight(request,member);

        //then
        verify(highlightManager, times(1)).selectHighlight(request,member);
    }

    @DisplayName("manager.uploadHighlights(UploadHighlight,List<MultipartFile>)가 실행되는지 검증합니다.")
    @Test
    void uploadHighlights(){
        //given
        UploadHighlight request=UploadHighlight.builder()
                .highlightKey("UUID")
                .build();
        List<MultipartFile> list=new ArrayList<>();
        UUID memberId=UUID.randomUUID();

        //when
        commandService.uploadHighlights(request,list,memberId);

        //then
        verify(highlightManager,times(1)).uploadHighlights(request,list,memberId);
    }
}
