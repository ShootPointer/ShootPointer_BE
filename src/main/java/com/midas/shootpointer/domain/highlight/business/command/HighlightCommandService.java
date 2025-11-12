package com.midas.shootpointer.domain.highlight.business.command;

import com.midas.shootpointer.domain.highlight.dto.HighlightRequest;
import com.midas.shootpointer.domain.highlight.dto.HighlightSelectRequest;
import com.midas.shootpointer.domain.highlight.dto.HighlightSelectResponse;
import com.midas.shootpointer.domain.member.entity.Member;

import java.util.UUID;

public interface HighlightCommandService {
    HighlightSelectResponse selectHighlight(HighlightSelectRequest request, Member member);

    void uploadHighlights(HighlightRequest highlights, UUID memberId);
}
////