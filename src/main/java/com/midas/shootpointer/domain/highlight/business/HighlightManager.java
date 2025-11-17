package com.midas.shootpointer.domain.highlight.business;

import com.midas.shootpointer.domain.backnumber.entity.BackNumberEntity;
import com.midas.shootpointer.domain.highlight.dto.*;
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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.TreeMap;
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
    * 1. openCv 에서 생성한 하이라이트 영상 주소 -> DB에 하이라이트 URL 저장
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
        List<HighlightEntity> entities=factory.createHighlightEntities(request.getHighlightUrls(),request.getHighlightIdentifier(),member,backNumber,request.getCreatedAt());

        /*
            3. DB 저장
         */
        highlightHelper.savedAll(entities);
    }

    public Page<HighlightInfoResponse> listByPaging(int page, int size,UUID memberId) {
        /**
         * 1. Paging 처리
         */
        Pageable paging= PageRequest.of(page,size);

        /**
         * 2. member의 모든 하이라이트 리스트 조회
         */
        Page<HighlightEntity> highlightEntityList=highlightHelper.fetchMembersHighlights(memberId,paging);

        /**
         * 3. mapping
         */
        return highlightEntityList.map(mapper::infoResponseToEntity);
    }

    public List<PeriodHighlightResponse> fetchAllMembersHighlights(String period){
        PeriodType convertedType=PeriodType.valueOf(period);
        return highlightHelper.fetchAllMembersHighlights(convertedType);
    }

    public HighlightCalendarResponse fetchCalendar(int year, int month,UUID memberId) {
        /**
         * 1. year,month 입력 값 검증
         */
        highlightHelper.isValidDateRange(year,month);

        /**
         * 2.년 월 기간 내 생성된 하이라이트 영상 조회 - flat data 조회
         */
        List<HighlightInfoResponse> flatHighlightList=highlightHelper.fetchFlatHighlightList(year,month,memberId);

        /**
         * 3. flat data 그룹핑한 데이터 조회
         */
        TreeMap<LocalDate,List<HighlightInfoResponse>> groupingHighlights=highlightHelper.groupingHighlights(flatHighlightList);

        /**
         * 4.date와 매핑된 데이터 반환
         */
        List<HighlightCalendarDaysResponse> daysResponses=mapper.groupingHighlightToDaysResponse(groupingHighlights);

        return new HighlightCalendarResponse(year,month,daysResponses);
    }
}
