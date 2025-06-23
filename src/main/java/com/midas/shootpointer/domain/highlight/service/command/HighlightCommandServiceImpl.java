package com.midas.shootpointer.domain.highlight.service.command;

import com.midas.shootpointer.domain.highlight.dto.HighlightResponse;
import com.midas.shootpointer.domain.highlight.dto.HighlightSelectRequest;
import com.midas.shootpointer.domain.highlight.entity.HighlightEntity;
import com.midas.shootpointer.domain.highlight.repository.HighlightCommandRepository;
import com.midas.shootpointer.domain.highlight.repository.HighlightQueryRepository;
import com.midas.shootpointer.domain.member.repository.MemberRepository;
import com.midas.shootpointer.global.annotation.CustomLog;
import com.midas.shootpointer.global.common.ErrorCode;
import com.midas.shootpointer.global.exception.CustomException;
import com.midas.shootpointer.global.util.jwt.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class HighlightCommandServiceImpl implements HighlightCommandService{
    private final HighlightCommandRepository highlightCommandRepository;
    private final HighlightQueryRepository highlightFetchRepository;
    private final MemberRepository memberRepository;
    private final JwtUtil jwtUtil;
    @Override
    @CustomLog
    public HighlightResponse selectHighlight(HighlightSelectRequest request,String token) {
        List<UUID> selectedHighlights=request.getSelectedHighlightIds();

        //1. 하이라이트Id로 하이라이트 엔티티 가져오기
        List<HighlightEntity> findByHighlightEntities = selectedHighlights.stream()
                .map(id -> highlightFetchRepository.findByHighlightId(id)
                        .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_HIGHLIGHT_ID)))
                .toList();
        //TODO:선택할 때 자신의 하이라이트 영상인지 확인 절차 필요

        //2. 가져온 하이라이트의 is_selected를 true로 변환
        findByHighlightEntities.forEach(HighlightEntity::select);

        //3. 변경 사항 저장
        highlightCommandRepository.saveAll(findByHighlightEntities);
        return null;
    }
}
