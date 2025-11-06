package com.midas.shootpointer.domain.highlight.business;

import com.midas.shootpointer.domain.backnumber.entity.BackNumberEntity;
import com.midas.shootpointer.domain.highlight.dto.HighlightRequest;
import com.midas.shootpointer.domain.highlight.dto.HighlightSelectRequest;
import com.midas.shootpointer.domain.highlight.dto.HighlightSelectResponse;
import com.midas.shootpointer.domain.highlight.entity.HighlightEntity;
import com.midas.shootpointer.domain.highlight.helper.HighlightHelper;
import com.midas.shootpointer.domain.highlight.mapper.HighlightFactory;
import com.midas.shootpointer.domain.highlight.mapper.HighlightMapper;
import com.midas.shootpointer.domain.member.entity.Member;
import com.midas.shootpointer.domain.member.helper.MemberHelper;
import com.midas.shootpointer.domain.memberbacknumber.helper.MemberBackNumberHelper;
import com.midas.shootpointer.global.annotation.CustomLog;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
public class HighlightManager {
    private final HighlightHelper highlightHelper;
    private final HighlightMapper mapper;
    private final MemberBackNumberHelper memberBackNumberHelper;
    private final HighlightFactory factory;
    private final MemberHelper memberHelper;

    /*==========================
    *
    *HighlightManager
    * 여러개의 하이라이트 영상 중 유저가 선택하는 메서드
    * @parm request : 요청 dto memberId : 멤버 Id
    * @return 선택된 하이라이트 영상 Id 리스트
    * @author kimdoyeon
    * @version 1.0.0
    * @date 25. 10. 7.
    *
    ==========================**/
    @Transactional
    @CustomLog
    public HighlightSelectResponse selectHighlight(HighlightSelectRequest request, Member member){
        List<UUID> selectedIds=request.getSelectedHighlightIds();
        /**
         * 1. 유저가 선택 요청한 하이라이트 Id 리스트 -> 엔티티로 가져오기
         */
        List<HighlightEntity> highlights = selectedIds.stream()
                .map(highlightHelper::findHighlightByHighlightId)
                .toList();

        /*
         * 2. 선택 수행
         */
        highlights.forEach(entity -> entity.select(member));

        return mapper.entityToResponse(selectedIds);
    }

    /*==========================
    *
    *HighlightManager
    * 1. openCv 에서 생성한 하이라이트 영상들을 비디오 디렉토리에 저장.
    * 2. PostgreSQL 디렉토리 URL 및 하이라이트 정보 저장.
    * @parm
    * @return
    * @author kimdoyeon
    * @version 1.0.0
    * @date 25. 10. 7.
    *
    ==========================**/
    @CustomLog
    @Transactional
    public void saveHighlights(HighlightRequest request, UUID memberId) {

        /**
         * 1. Member 검증 및 조회
         */
        Member member = memberHelper.findMemberById(memberId);

        /**
         * 2. BackNumber 검증 및 조회
         */
        BackNumberEntity backNumber=memberBackNumberHelper.findByMemberId(memberId);

        /*
        *   2. 하이라이트 엔티티 생성
         */
        List<HighlightEntity> entities=factory.createHighlightEntities(request.getHighlightUrls(),request.getHighlightIdentifier(),member,backNumber);

        /*
            3. DB 저장
         */
        highlightHelper.savedAll(entities);
    }
}
