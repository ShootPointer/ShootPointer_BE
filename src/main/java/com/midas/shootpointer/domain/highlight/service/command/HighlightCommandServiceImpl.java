package com.midas.shootpointer.domain.highlight.service.command;

import com.midas.shootpointer.domain.highlight.dto.HighlightResponse;
import com.midas.shootpointer.domain.highlight.dto.HighlightSelectRequest;
import com.midas.shootpointer.domain.highlight.dto.HighlightSelectResponse;
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
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class HighlightCommandServiceImpl implements HighlightCommandService{
    private final HighlightCommandRepository highlightCommandRepository;
    private final HighlightQueryRepository highlightFetchRepository;
    private final MemberRepository memberRepository;
    private final JwtUtil jwtUtil;
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
    @CustomLog
    @Transactional
    public HighlightSelectResponse selectHighlight(HighlightSelectRequest request, String token) {
        List<UUID> selectedHighlights=request.getSelectedHighlightIds();
        UUID memberId=jwtUtil.getMemberId();

        //1. 하이라이트Id로 하이라이트 엔티티 가져오기
        List<HighlightEntity> findByHighlightEntities = selectedHighlights.stream()
                .map(id -> highlightFetchRepository.findByHighlightId(id)
                        .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_HIGHLIGHT_ID))).toList();

        //2. 선택한 하이라이트 영상이 요청자의 하이라이트 영상 확인
        findByHighlightEntities.forEach(h-> isHighlightVideoSameMember(h.getHighlightId(),memberId));

        //3. 가져온 하이라이트의 is_selected를 true로 변환
        findByHighlightEntities.forEach(HighlightEntity::select);

        //4. 변경 사항 저장
        highlightCommandRepository.saveAll(findByHighlightEntities);

        return HighlightSelectResponse.builder()
                .selectedHighlightIds(selectedHighlights)
                .build();
    }

    /*==========================
    *
    *HighlightCommandServiceImpl
    *
    * @parm highlightId : 하이라이트 엔티티 Id  , memberId : 멤버 엔티티 Id
    * @return void
    * @author kimdoyeon
    * @version 1.0.0
    * @date 6/23/25
    *
    ==========================**/
    private void isHighlightVideoSameMember(UUID highlightId,UUID memberId){
        if(!highlightFetchRepository.isMembersHighlight(memberId,highlightId)){
            throw new CustomException(ErrorCode.NOT_MATCH_HIGHLIGHT_VIDEO);
        }
    }
}
