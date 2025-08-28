package com.midas.shootpointer.domain.highlight.business.command;

import com.midas.shootpointer.domain.highlight.dto.HighlightResponse;
import com.midas.shootpointer.domain.highlight.dto.HighlightSelectRequest;
import com.midas.shootpointer.domain.highlight.dto.HighlightSelectResponse;
import com.midas.shootpointer.domain.highlight.dto.UploadHighlight;
import com.midas.shootpointer.domain.highlight.entity.HighlightEntity;
import com.midas.shootpointer.domain.highlight.repository.HighlightCommandRepository;
import com.midas.shootpointer.domain.highlight.repository.HighlightQueryRepository;
import com.midas.shootpointer.domain.member.repository.MemberRepository;
import com.midas.shootpointer.global.annotation.CustomLog;
import com.midas.shootpointer.global.common.ErrorCode;
import com.midas.shootpointer.global.exception.CustomException;
import com.midas.shootpointer.global.util.jwt.JwtUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class HighlightCommandServiceImpl implements HighlightCommandService {
    private final HighlightCommandRepository highlightCommandRepository;
    private final HighlightQueryRepository highlightQueryRepository;
    private final MemberRepository memberRepository;
    private final JwtUtil jwtUtil;
    /*
    영상 크기 제한 100MB
     */
    private static final long MAX_FILE_SIZE = 100L * 1024L * 1024L;

    //영상 저장 경로
    @Value("${video.path}")
    private String videoPath;

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
        List<UUID> selectedHighlights = request.getSelectedHighlightIds();
        UUID memberId = jwtUtil.getMemberId(token);

        //1. 하이라이트Id로 하이라이트 엔티티 가져오기
        List<HighlightEntity> findByHighlightEntities = selectedHighlights.stream()
                .map(h -> highlightQueryRepository.findByHighlightId(h)
                        .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_HIGHLIGHT_ID))).toList();

        //2. 선택한 하이라이트 영상이 요청자의 하이라이트 영상 확인
        findByHighlightEntities.forEach(h -> {
            isHighlightVideoSameMember(h.getHighlightId(), memberId);
            //3. 가져온 하이라이트의 is_selected를 true로 변환
            h.select();
            highlightCommandRepository.save(h);
        });

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
    * @parm 
    * @return 
    * @author kimdoyeon
    * @version 1.0.0
    * @date 6/23/25
    *
    ==========================**/
    @Override
    @CustomLog
    @Transactional
    public List<HighlightResponse> uploadHighlights(String memberId, String token, UploadHighlight request,List<MultipartFile> highlights) {
        /**
         *   1.OpenCV에서 받은 하이라이트 영상을 저장.
         */
        //파일 크기 및 파일 형식 검사
        highlights.forEach(highlight -> {
                    isValidFileSize(highlight);
                    isValidFileType(highlight);
                }
        );
        List<HighlightEntity> highlightEntities = new ArrayList<>();
        String highlightKey = request.getHighlightKey();
        String directoryPath = getDirectoryPath(highlightKey);

        highlights.forEach(h -> {
            String fileName = UUID.randomUUID() + ".mp4";
            Path filePath = Paths.get(directoryPath, fileName);
            try (OutputStream os = Files.newOutputStream(filePath)) {
                os.write(h.getBytes());
                highlightEntities.add(
                        HighlightEntity.builder()
                                .highlightURL(fileName)
                                .highlightKey(UUID.fromString(highlightKey))
                                .build()
                );
            } catch (IOException e) {
                log.error("uploadHighlights : method {} : message",e.getMessage());
                throw new CustomException(ErrorCode.FILE_UPLOAD_FAILED);
            }
        });

        List<HighlightEntity> savedHighlights = highlightCommandRepository.saveAll(highlightEntities);

        //2. 하이라이트 저장 시 멤버 및 키값을 통해 매핑.
        List<HighlightResponse> responses = new ArrayList<>();

        savedHighlights
                .forEach(r -> {
                    HighlightResponse response=HighlightResponse.builder()
                            .highlightId(r.getHighlightId())
                            .highlightIdentifier(r.getHighlightKey())
                            .highlightUrl(r.getHighlightURL())
                            .build();

                    responses.add(response);
                });

        //3. 저장한 하이라이트 영상 URL 반환.
        return responses;
    }

    /**
     * 유저의 하이라이트 영상인지 확인 메서드
     *
     * @param highlightId 하이라이트 id
     * @param memberId    유저의 Id
     */
    private void isHighlightVideoSameMember(UUID highlightId, UUID memberId) {
        if (!highlightQueryRepository.isMembersHighlight(memberId, highlightId)) {
            throw new CustomException(ErrorCode.NOT_MATCH_HIGHLIGHT_VIDEO);
        }
    }

    /**
     * 파일 타입 체크 메서드
     *
     * @param file 파일
     */
    private void isValidFileType(MultipartFile file) {
        String contentType = file.getContentType();

        //mp4 파일 형식 검사.
        if (!contentType.equals("video/mp4")) {
            throw new CustomException(ErrorCode.INVALID_FILE_TYPE);
        }
    }

    /**
     * 파일 사이즈 체크 메서드
     *
     * @param file 파일
     */

    private void isValidFileSize(MultipartFile file) {
        long fileSize = file.getSize();
        //파일 사이즈 제한 - 500MB
        if (fileSize >= MAX_FILE_SIZE) {
            throw new CustomException(ErrorCode.FILE_SIZE_EXCEEDED);
        }
    }

    private String getDirectoryPath(String highlightKey) {
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
}
