package com.midas.shootpointer.domain.highlight.business;

import com.midas.shootpointer.domain.highlight.dto.HighlightResponse;
import com.midas.shootpointer.domain.highlight.dto.HighlightSelectRequest;
import com.midas.shootpointer.domain.highlight.dto.HighlightSelectResponse;
import com.midas.shootpointer.domain.highlight.dto.UploadHighlight;
import com.midas.shootpointer.domain.highlight.entity.HighlightEntity;
import com.midas.shootpointer.domain.highlight.repository.HighlightCommandRepository;
import com.midas.shootpointer.domain.member.entity.Member;
import com.midas.shootpointer.domain.member.repository.MemberCommandRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * 통합 테스트 진행
 */
@SpringBootTest
@ActiveProfiles("test")
class HighlightManagerTest {
    @Autowired
    private HighlightManager highlightManager;

    @Autowired
    private HighlightCommandRepository repository;

    @Autowired
    private MemberCommandRepository memberCommandRepository;

    @TempDir
    private static Path tempDir;

    @DynamicPropertySource
    static void registerProperties(DynamicPropertyRegistry registry) {
        registry.add("video.path", () -> tempDir.toString());
    }

    @AfterEach
    void cleanUp(){
        repository.deleteAll();
    }

    @Test
    @DisplayName("하이라이트 영상 객체를 호출하고 가져온 하이라이트 영상의 is_selected를 true 상태로 변환합니다.")
    void selectHighlight(){
        //given
        //하이라이트 영상 URL 저장
        UUID highlightKey=UUID.randomUUID();
        Member member=memberCommandRepository.save(makeMember());
        List<HighlightEntity> highlightEntities=List.of(
                makeHighlightEntity("url",highlightKey,member),
                makeHighlightEntity("url",highlightKey,member),
                makeHighlightEntity("url",highlightKey,member)
        );
        highlightEntities=repository.saveAll(highlightEntities);
        List<UUID> selectedIds=highlightEntities.stream()
                .map(HighlightEntity::getHighlightId)
                .toList();

        HighlightSelectRequest request=HighlightSelectRequest.builder()
                .selectedHighlightIds(selectedIds)
                .build();


        //when
        HighlightSelectResponse response=highlightManager.selectHighlight(request,member);

        //then
        assertThat(response).isNotNull();
        assertThat(response.getSelectedHighlightIds()).hasSize(3);
        assertThat(response.getSelectedHighlightIds().get(0)).isEqualTo(selectedIds.get(0));
        assertThat(response.getSelectedHighlightIds().get(1)).isEqualTo(selectedIds.get(1));
        assertThat(response.getSelectedHighlightIds().get(2)).isEqualTo(selectedIds.get(2));

    }


    @Test
    @DisplayName("실제 하이라이트 영상을 저장하고 엔티티를 DB에 저장한 후 DTO로 변환합니다.")
    void uploadHighlights(){
        //given
        Member member=memberCommandRepository.save(
                Member.builder()
                        .username("test")
                        .email("test@naver.com")
                        .build()
        );
        UUID highlightKey=UUID.randomUUID();
        LocalDateTime now=LocalDateTime.now();
        UploadHighlight request=UploadHighlight.builder()
                .highlightKey(highlightKey.toString())
                .createAt(now)
                .build();
        byte[] content=new byte[100];
        MultipartFile file1=new MockMultipartFile("file1","video1.mp4","video/mp4",content);
        MultipartFile file2=new MockMultipartFile("file2","video2.mp4","video/mp4",content);
        List<MultipartFile> files=List.of(file1,file2);


        //when
        List<HighlightResponse> result=highlightManager.uploadHighlights(request,files,member.getMemberId());

        //then
        assertThat(result).hasSize(2);
        assertThat(result.get(0).getHighlightUrl()).contains(".mp4");
        assertThat(result.get(1).getHighlightUrl()).contains(".mp4");

        //실제 디렉ㅌ리 생성 여부
        Path highlightDir=tempDir.resolve(highlightKey.toString());
        assertThat(Files.exists(highlightDir)).isTrue();

        //저장된 파일 존재 여부 검증
        try (var stream = Files.list(highlightDir)) {
            List<String> storedFiles = stream.map(Path::getFileName)
                    .map(Path::toString)
                    .toList();
            assertThat(storedFiles).hasSize(2);
            assertThat(storedFiles.get(0)).endsWith(".mp4");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        //DB 저장 검증
        List<HighlightEntity> saved = repository.findAll();
        assertThat(saved).hasSize(2);
        assertThat(saved.get(0).getHighlightKey()).isEqualTo(highlightKey);
    }
    private HighlightEntity makeHighlightEntity(String url,UUID highlightKey,Member member){
        return HighlightEntity.builder()
                .highlightURL(url)
                .highlightKey(highlightKey)
                .member(member)
                .build();
    }

    private Member makeMember(){
        return Member.builder()
                .email("test@naver.com")
                .username("test")
                .build();
    }
}