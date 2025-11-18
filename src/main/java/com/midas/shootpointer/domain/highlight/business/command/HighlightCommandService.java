package com.midas.shootpointer.domain.highlight.business.command;

import com.midas.shootpointer.domain.highlight.dto.HighlightRequest;

import java.util.UUID;

public interface HighlightCommandService {
    void uploadHighlights(HighlightRequest highlights, UUID memberId);
}
