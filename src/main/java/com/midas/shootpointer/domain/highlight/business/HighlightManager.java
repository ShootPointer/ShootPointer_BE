package com.midas.shootpointer.domain.highlight.business;

import com.midas.shootpointer.domain.highlight.dto.HighlightResponse;
import com.midas.shootpointer.domain.highlight.dto.HighlightSelectRequest;
import com.midas.shootpointer.domain.highlight.dto.HighlightSelectResponse;
import com.midas.shootpointer.domain.highlight.dto.UploadHighlight;
import com.midas.shootpointer.domain.highlight.entity.HighlightEntity;
import com.midas.shootpointer.domain.highlight.helper.HighlightHelper;
import com.midas.shootpointer.domain.highlight.mapper.HighlightFactory;
import com.midas.shootpointer.domain.highlight.mapper.HighlightMapper;
import com.midas.shootpointer.domain.highlight.service.HighlightStorageService;
import com.midas.shootpointer.domain.member.entity.Member;
import com.midas.shootpointer.global.annotation.CustomLog;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
public class HighlightManager {
    private final HighlightHelper highlightHelper;
    private final HighlightStorageService highlightStorageService;
    private final HighlightMapper mapper;
    private final HighlightFactory factory;

    @Value("${video.path}")
    private String videoPath;

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
    public List<HighlightResponse> uploadHighlights(
            UploadHighlight request,
            List<MultipartFile> highlights
    ) {
        /**
         * 1. 파일 크기 및 파일 형식 검사
         */
        highlightHelper.areValidFiles(highlights);

        /**
         * 2. 하이라이트 키 / 디렉토리 -> 하이라이트 엔티티 저장
         */
        List<String> storedFiles=highlightStorageService.storeHighlights(highlights,request.getHighlightKey());

        /*
        *   3. 하이라이트 엔티티 생성
         */
        List<HighlightEntity> entities=factory.createHighlightEntities(storedFiles,request.getHighlightKey());

        /*
            4. DB 저장
         */
        List<HighlightEntity> savedEntities = highlightHelper.savedAll(entities);

        return savedEntities.stream()
                .map(mapper::entityToResponse)
                .toList();
    }
}
