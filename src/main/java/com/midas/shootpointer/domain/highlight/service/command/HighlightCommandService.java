package com.midas.shootpointer.domain.highlight.service.command;

import com.midas.shootpointer.domain.highlight.dto.HighlightResponse;
import com.midas.shootpointer.domain.highlight.dto.HighlightSelectRequest;
import com.midas.shootpointer.domain.highlight.dto.HighlightSelectResponse;
import com.midas.shootpointer.domain.highlight.dto.UploadHighlight;

import java.util.List;
import java.util.UUID;

public interface HighlightCommandService {
    HighlightSelectResponse selectHighlight(HighlightSelectRequest request, String token);

    List<HighlightResponse> uploadHighlights(UUID memberId, String token, UploadHighlight request);
}
