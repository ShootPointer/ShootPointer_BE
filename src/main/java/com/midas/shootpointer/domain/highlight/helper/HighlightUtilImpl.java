package com.midas.shootpointer.domain.highlight.helper;

import com.midas.shootpointer.domain.highlight.dto.DateTimeRange;
import com.midas.shootpointer.domain.highlight.dto.HighlightInfoResponse;
import com.midas.shootpointer.domain.highlight.dto.PeriodHighlightResponse;
import com.midas.shootpointer.domain.highlight.dto.PeriodType;
import com.midas.shootpointer.domain.highlight.entity.HighlightEntity;
import com.midas.shootpointer.domain.highlight.repository.HighlightCommandRepository;
import com.midas.shootpointer.domain.highlight.repository.HighlightQueryRepository;
import com.midas.shootpointer.global.common.ErrorCode;
import com.midas.shootpointer.global.exception.CustomException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

@Component
@Slf4j
public class HighlightUtilImpl implements HighlightUtil{
    //영상 저장 경로
    private final String videoPath;

    private final HighlightQueryRepository highlightQueryRepository;

    private final HighlightCommandRepository highlightCommandRepository;

    private static final int FETCH_SIZE=10;

    private static final int HIGHLIGHT_SIZE=3;

    public HighlightUtilImpl(@Value("${video.path}") String videoPath, HighlightQueryRepository highlightQueryRepository, HighlightCommandRepository highlightCommandRepository){
        this.videoPath=videoPath;
        this.highlightQueryRepository = highlightQueryRepository;
        this.highlightCommandRepository = highlightCommandRepository;
    }

    @Override
    public String getDirectoryPath(String highlightKey) {
        String directory = videoPath + "/" + highlightKey;
        Path directoryPath = Paths.get(directory);
        if (!Files.exists(directoryPath)) {
            try {
                Files.createDirectories(directoryPath);
            } catch (IOException e) {
                log.error("method : getDirectoryPath message : {}",e.getMessage());
                throw new CustomException(ErrorCode.FILE_UPLOAD_FAILED);
            }
        }
        return directoryPath.toString();
    }

    @Override
    public HighlightEntity findHighlightByHighlightId(UUID highlightId) {
        return highlightQueryRepository.findByHighlightId(highlightId)
                .orElseThrow(()->new CustomException(ErrorCode.NOT_EXIST_HIGHLIGHT));
    }

    @Override
    public List<HighlightEntity> savedAll(List<HighlightEntity> entities) {
        return highlightCommandRepository.saveAll(entities);
    }

    @Override
    public Page<HighlightEntity> fetchMembersHighlights(UUID memberId, Pageable pageable) {
        return highlightQueryRepository.fetchAllMembersHighlights(memberId, pageable);
    }

    @Override
    public List<PeriodHighlightResponse> fetchAllMembersHighlights(PeriodType period) {
        LocalDateTime now=LocalDateTime.now();
        LocalDateTime startDate=calculateDateTimeRange(period,now).start();
        LocalDateTime endDate=calculateDateTimeRange(period,now).end();
        Pageable page= PageRequest.of(0,HIGHLIGHT_SIZE);

        return highlightQueryRepository.fetchPeriodHighlight(startDate,endDate,FETCH_SIZE,page);
    }

    /**
     * @param flatHighlightList 년 월 기간 내 생성된 하이라이트 영상 목록
     * @return LocalDate(ex. 2022-10-22T) 형태로 그룹핑
     */
    @Override
    public TreeMap<LocalDate, List<HighlightInfoResponse>> groupingHighlights(List<HighlightInfoResponse> flatHighlightList) {
        TreeMap<LocalDate,List<HighlightInfoResponse>> results=new TreeMap<>();

        for (HighlightInfoResponse response:flatHighlightList){
            //LocalDateTime -> LocalDate
            LocalDate date=response.createdDate().toLocalDate();

            //key 매핑
            results.putIfAbsent(date,new ArrayList<>());

            //value 삽입
            results.get(date).add(response);
        }

        //flatHighlightList는 오름차순으로 정렬되어 반환되므로 따로 정렬할 필요 없음.
        return results;
    }

    /**
     * @param year 연도
     * @param month 달
     * @param memberId 멤버 ID
     * @return 유저의 입력된 년 월 기간 내 생성된 하이라이트 영상 조회
     */
    @Override
    public List<HighlightInfoResponse> fetchFlatHighlightList(int year, int month,UUID memberId) {
        DateTimeRange range=getMonthDateTimeRange(year,month);
        return highlightQueryRepository.fetchFlatHighlights(range.start(),range.end(),memberId);
    }

    @Override
    public DateTimeRange calculateDateTimeRange(PeriodType type, LocalDateTime now) {
        LocalDateTime start;
        LocalDateTime end;

        switch (type){
            case MONTHLY -> {
                start= now.withDayOfMonth(1).toLocalDate().atStartOfDay();
                end=now.withDayOfMonth(now.toLocalDate().lengthOfMonth())
                        .toLocalDate()
                        .atTime(23,59,59);
            }
            case WEEKLY -> {
                start=now
                        .with(DayOfWeek.MONDAY)
                        .toLocalDate()
                        .atStartOfDay();
                end=now.with(DayOfWeek.SUNDAY)
                        .toLocalDate()
                        .atTime(23,59,59);
            }
            case DAILY -> {
                start= now.toLocalDate()
                        .atStartOfDay();
                end= now.toLocalDate()
                        .atTime(23,59,59);
            }
            default -> throw new IllegalArgumentException("LocalDateTime 지원하지 않는 타입");
        }
        return new DateTimeRange(start,end);
    }

    @Override
    public DateTimeRange getMonthDateTimeRange(int year, int month) {
        LocalDate startDate = LocalDate.of(year, month, 1);
        LocalDate endDate = startDate.withDayOfMonth(startDate.lengthOfMonth());

        LocalDateTime start = startDate.atStartOfDay();
        LocalDateTime end = endDate.atTime(23, 59, 59);
        return new DateTimeRange(start,end);
    }

    @Override
    public List<HighlightEntity> fetchLastestCreatedHighlights(String jobId, UUID memberId) {
        return highlightQueryRepository.fetchHighlightsByJobId(jobId,memberId);
    }


}
