package com.midas.shootpointer.domain.highlight.business.command;

import com.midas.shootpointer.domain.highlight.dto.HighlightResponse;
import com.midas.shootpointer.domain.highlight.dto.HighlightSelectRequest;
import com.midas.shootpointer.domain.highlight.dto.HighlightSelectResponse;
import com.midas.shootpointer.domain.highlight.dto.UploadHighlight;
import com.midas.shootpointer.domain.member.entity.Member;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;

public interface HighlightCommandService {
    HighlightSelectResponse selectHighlight(HighlightSelectRequest request, Member member);

    List<HighlightResponse> uploadHighlights(UploadHighlight request, List<MultipartFile> highlights, UUID memberId);
}
////