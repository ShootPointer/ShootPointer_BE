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
