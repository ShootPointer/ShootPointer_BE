package com.midas.shootpointer.domain.highlight.helper;

import com.midas.shootpointer.domain.highlight.dto.DateTimeRange;
import com.midas.shootpointer.domain.highlight.dto.HighlightInfoResponse;
import com.midas.shootpointer.domain.highlight.dto.PeriodHighlightResponse;
import com.midas.shootpointer.domain.highlight.dto.PeriodType;
import com.midas.shootpointer.domain.highlight.entity.HighlightEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.TreeMap;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class HighlightHelperImpl implements HighlightHelper{
    private final HighlightUtil highlightUtil;
    private final HighlightValidator highlightValidator;
    @Override
    public String getDirectoryPath(String highlightKey) {
        return highlightUtil.getDirectoryPath(highlightKey);
    }

    @Override
    public HighlightEntity findHighlightByHighlightId(UUID highlightId) {
        return highlightUtil.findHighlightByHighlightId(highlightId);
    }

    @Override
    public List<HighlightEntity> savedAll(List<HighlightEntity> entities) {
        return highlightUtil.savedAll(entities);
    }

    @Override
    public Page<HighlightEntity> fetchMembersHighlights(UUID memberId, Pageable pageable) {
        return highlightUtil.fetchMembersHighlights(memberId,pageable);
    }

    @Override
    public List<PeriodHighlightResponse> fetchAllMembersHighlights(PeriodType period) {
        return highlightUtil.fetchAllMembersHighlights(period);
    }

    @Override
    public DateTimeRange calculateDateTimeRange(PeriodType type, LocalDateTime now) {
        return highlightUtil.calculateDateTimeRange(type,now);
    }

    @Override
    public TreeMap<LocalDate, List<HighlightInfoResponse>> groupingHighlights(List<HighlightInfoResponse> flatHighlightList) {
        return highlightUtil.groupingHighlights(flatHighlightList);
    }

    @Override
    public List<HighlightInfoResponse> fetchFlatHighlightList(int year, int month, UUID memberId) {
        return highlightUtil.fetchFlatHighlightList(year,month,memberId);
    }

    @Override
    public DateTimeRange getMonthDateTimeRange(int year, int month) {
        return highlightUtil.getMonthDateTimeRange(year,month);
    }

    @Override
    public List<HighlightEntity> fetchLastestCreatedHighlights(UUID jobId, UUID memberId) {
        return highlightUtil.fetchLastestCreatedHighlights(jobId,memberId);
    }

    @Override
    public boolean filesExist(String directory) {
        return highlightValidator.filesExist(directory);
    }

    @Override
    public void isExistHighlightId(UUID highlightId) {
        highlightValidator.isExistHighlightId(highlightId);
    }

    @Override
    public void isValidMembersHighlight(UUID highlightId, UUID memberId) {
        highlightValidator.isValidMembersHighlight(highlightId,memberId);
    }

    @Override
    public void isValidMp4File(MultipartFile file) {
        highlightValidator.isValidMp4File(file);
    }

    @Override
    public void isValidFileSize(MultipartFile file) {
        highlightValidator.isValidFileSize(file);
    }

    @Override
    public boolean isExistDirectory(String directory) {
        return highlightValidator.isExistDirectory(directory);
    }

    @Override
    public void areValidFiles(List<MultipartFile> files) {
        highlightValidator.areValidFiles(files);
    }

    @Override
    public void isValidDateRange(int year, int month) {
        highlightValidator.isValidDateRange(year,month);
    }
}
