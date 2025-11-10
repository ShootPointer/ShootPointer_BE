package com.midas.shootpointer.domain.highlight.business;

import com.midas.shootpointer.domain.highlight.dto.HighlightInfoResponse;
import com.midas.shootpointer.domain.highlight.dto.HighlightSelectRequest;
import com.midas.shootpointer.domain.highlight.dto.HighlightSelectResponse;
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
import org.springframework.data.domain.Page;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;

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
class HighlightManagerTest  {
    @Autowired
    private HighlightManager highlightManager;

    @Autowired
    private HighlightCommandRepository repository;

    @Autowired
    private MemberCommandRepository memberCommandRepository;

    @Autowired
    private HighlightCommandRepository highlightCommandRepository;

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

    }


    @Test
    @DisplayName("회원별 하이라이트 페이징 조회를 실시합니다.")
    void listByPagingHighlights(){
        // given
        Member member = memberCommandRepository.save(Member.builder()
                .email("test@naver.com")
                .username("tester")
                .isAggregationAgreed(true)
                .build());

        // DB에 10개의 하이라이트 데이터 저장
        for (int i = 1; i <= 10; i++) {
            HighlightEntity entity=HighlightEntity.builder()
                    .highlightKey(UUID.randomUUID())
                    .highlightURL("https://cdn.example.com/video" + i + ".mp4")
                    .isSelected(true)
                    .member(member)
                    .build();
            entity.setCreatedAt(LocalDateTime.now().minusDays(i));

            highlightCommandRepository.save(entity);
        }

        // when
        Page<HighlightInfoResponse> result = highlightManager.listByPaging(0, 5, member.getMemberId());

        // then
        assertThat(result).isNotNull();
        assertThat(result.getContent()).hasSize(5); // 첫 페이지 5개
        assertThat(result.getTotalElements()).isEqualTo(10); // 전체 10개
        assertThat(result.getContent().getFirst().highlightUrl()).contains(".mp4");
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