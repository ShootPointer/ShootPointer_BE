package com.midas.shootpointer.domain.highlight.service.command;

import com.midas.shootpointer.domain.highlight.dto.HighlightResponse;
import com.midas.shootpointer.domain.highlight.dto.HighlightSelectRequest;

public interface HighlightCommandService {
    HighlightResponse selectHighlight(HighlightSelectRequest request,String token);
}
