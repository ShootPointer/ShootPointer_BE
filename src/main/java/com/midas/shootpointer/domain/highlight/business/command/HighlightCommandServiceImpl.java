package com.midas.shootpointer.domain.highlight.business.command;

import com.midas.shootpointer.domain.highlight.business.HighlightManager;
import com.midas.shootpointer.domain.highlight.dto.HighlightRequest;
import com.midas.shootpointer.domain.highlight.dto.HighlightSelectRequest;
import com.midas.shootpointer.domain.highlight.dto.HighlightSelectResponse;
import com.midas.shootpointer.domain.member.entity.Member;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class HighlightCommandServiceImpl implements HighlightCommandService {
    private final HighlightManager manager;
    /*==========================
    *
    *HighlightCommandServiceImpl
    *
    * @parm HighlightSelectRequest : 하이라이트 선택 요청 Dto , token : JWT
    * @return 하이라이트 선택 성공 시 선택한 하이라이트 id 반환 dto
    * @author kimdoyeon
    * @version 1.0.0
    * @date 6/23/25
    *
    ==========================**/
    @Override
    public HighlightSelectResponse selectHighlight(HighlightSelectRequest request, Member member) {
        return manager.selectHighlight(request,member);
    }

    /*==========================
    *
    *HighlightCommandServiceImpl
    *
    * @parm 
    * @return 
    * @author kimdoyeon
    * @version 1.0.0
    * @date 6/23/25
    *
    ==========================**/
    @Override
    public void uploadHighlights(HighlightRequest highlights,UUID memberId) {
        manager.saveHighlights(highlights,memberId);
    }
}
