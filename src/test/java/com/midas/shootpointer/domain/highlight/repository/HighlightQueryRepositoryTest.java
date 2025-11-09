package com.midas.shootpointer.domain.highlight.repository;

import com.midas.shootpointer.domain.backnumber.entity.BackNumber;
import com.midas.shootpointer.domain.backnumber.entity.BackNumberEntity;
import com.midas.shootpointer.domain.backnumber.repository.BackNumberRepository;
import com.midas.shootpointer.domain.highlight.entity.HighlightEntity;
import com.midas.shootpointer.domain.member.entity.Member;
import com.midas.shootpointer.domain.member.repository.MemberQueryRepository;
import com.midas.shootpointer.domain.memberbacknumber.entity.MemberBackNumberEntity;
import com.midas.shootpointer.domain.memberbacknumber.repository.MemberBackNumberRepository;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
@DataJpaTest
@ActiveProfiles("dev")
class HighlightQueryRepositoryTest {
    @Autowired
    private EntityManager em;
    @Autowired
    private HighlightQueryRepository highlightQueryRepository;

    @Autowired
    private HighlightCommandRepository highlightCommandRepository;

    @Autowired
    private MemberQueryRepository memberRepository;

    @Autowired
    private BackNumberRepository backNumberRepository;

    @Autowired
    private MemberBackNumberRepository memberBackNumberRepository;

    @Test
    @DisplayName("멤버id와 하이라이트id가 일치하는 하이라이트 존재 시 true를 반환하는 쿼리입니다.")
    void isMemberHighlight_TRUE() {
        //given
        Member savedMember= memberRepository.save(mockMember());
        BackNumberEntity savedBackNumber= backNumberRepository.save(mockBackNumber());
        MemberBackNumberEntity memberBackNumberEntity=mockMemberBackNumber(savedMember,savedBackNumber);
        memberBackNumberRepository.save(memberBackNumberEntity);

        HighlightEntity highlight1=highlightCommandRepository.save(mockHighlight(savedMember,savedBackNumber));
        HighlightEntity highlight2=highlightCommandRepository.save(mockHighlight(savedMember,savedBackNumber));

        //when
        boolean isExistedHighlight1= highlightQueryRepository.isMembersHighlight(
                savedMember.getMemberId(),highlight1.getHighlightId()
        );
        boolean isExistedHighlight2= highlightQueryRepository.isMembersHighlight(
                savedMember.getMemberId(),highlight2.getHighlightId()
        );


        //then
        assertThat(isExistedHighlight1).isTrue();
        assertThat(isExistedHighlight2).isTrue();
    }

    @Test
    @DisplayName("멤버id와 하이라이트id가 일치하는 하이라이트 존재 하지 않을시 false를 반환하는 쿼리입니다.")
    void isMemberHighlight_FALSE() {
        //given
        Member savedMember1= memberRepository.save(mockMember());
        Member savedMember2= memberRepository.save(mockMember());

        BackNumberEntity savedBackNumber= backNumberRepository.save(mockBackNumber());
        MemberBackNumberEntity memberBackNumberEntity=mockMemberBackNumber(savedMember1,savedBackNumber);
        memberBackNumberRepository.save(memberBackNumberEntity);

        HighlightEntity highlight1=highlightCommandRepository.save(mockHighlight(savedMember1,savedBackNumber));
        HighlightEntity highlight2=highlightCommandRepository.save(mockHighlight(savedMember2,savedBackNumber));

        //when
        boolean isExistedHighlight1= highlightQueryRepository.isMembersHighlight(
                savedMember2.getMemberId(),highlight1.getHighlightId()
        );
        boolean isExistedHighlight2= highlightQueryRepository.isMembersHighlight(
                savedMember1.getMemberId(),highlight2.getHighlightId()
        );


        //then
        assertThat(isExistedHighlight1).isFalse();
        assertThat(isExistedHighlight2).isFalse();
    }

    @Test
    @DisplayName("유저의 하이라이트 영상이면 TRUE를 반환합니다.")
    void existsByHighlightUrlAndMember_TRUE(){
        //given
        Member mockMember=memberRepository.save(mockMember());
        BackNumberEntity mockBackNumber=backNumberRepository.save(mockBackNumber());
        HighlightEntity mockHighlight=highlightCommandRepository.save(mockHighlight(mockMember,mockBackNumber));

        //when
        boolean existsByHighlightUrlAndMember=highlightQueryRepository.existsByHighlightIdAndMember(mockHighlight.getHighlightId(),mockMember.getMemberId());

        //then
        assertThat(existsByHighlightUrlAndMember).isTrue();
    }


    @Test
    @DisplayName("유저의 하이라이트 영상이 아니면 FALSE를 반환합니다.")
    void existsByHighlightUrlAndMember_FALSE(){
        //given
        Member mockMember=memberRepository.save(mockMember());
        BackNumberEntity mockBackNumber=backNumberRepository.save(mockBackNumber());
        HighlightEntity mockHighlight=highlightCommandRepository.save(mockHighlight(mockMember,mockBackNumber));

        //when
        boolean existsByHighlightUrlAndMember=highlightQueryRepository.existsByHighlightIdAndMember(UUID.randomUUID(),mockMember.getMemberId());

        //then
        assertThat(existsByHighlightUrlAndMember).isFalse();
    }

    @Test
    @DisplayName("특정 멤버의 하이라이트를 페이지네이션 형태로 조회합니다.")
    void fetchAllMembersHighlightsPagination(){
        //given
        Member member=memberRepository.save(mockMember());
        Random random=new Random();
        List<HighlightEntity> expectedHighlights=new ArrayList<>();


        for (int idx=0;idx<33;idx++){
            HighlightEntity highlight=HighlightEntity.builder()
                    .highlightKey(UUID.randomUUID())
                    .member(member)
                    .threePointCount(random.nextInt(1,100))
                    .twoPointCount(random.nextInt(1,100))
                    .isSelected(true)
                    .highlightURL("test")
                    .build();
            highlight.setCreatedAt(LocalDateTime.now().minusDays(idx));
            highlight=highlightCommandRepository.save(highlight);

            expectedHighlights.add(highlight);
        }

        Pageable pageable= PageRequest.of(0,10);
        expectedHighlights.sort((a,b)->b.getCreatedAt().compareTo(a.getCreatedAt())); //생성 날짜 내림차순

        //when
        Page<HighlightEntity> result=highlightQueryRepository.fetchAllMembersHighlights(member.getMemberId(),pageable);

        //then
        assertThat(result).isNotNull();
        assertThat(result.getContent()).hasSize(10);
        assertThat(result.getTotalElements()).isEqualTo(33);//전체 개수
        assertThat(result.getTotalPages()).isEqualTo(4); //페이지 개수

        //최신순 정렬 확인
        for (int i=0;i<10;i++){
            HighlightEntity expected=expectedHighlights.get(i);

            assertThat(expected.getHighlightId()).isEqualTo(result.getContent().get(i).getHighlightId());
        }
    }
    /**
     * Mock Member
     */
    private Member mockMember(){
        Random random=new Random();
        return Member.builder()
                .email(random.nextInt(10) +"test@naver.com")
                .isAggregationAgreed(true)
                .username("테스터")
                .build();
    }

    /**
     * Mock Highlight
     */
    private HighlightEntity mockHighlight(Member member, BackNumberEntity backNumber){
        return HighlightEntity.builder()
                .highlightKey(UUID.randomUUID())
                .highlightURL("testURL")
                .backNumber(backNumber)
                .member(member)
                .build();
    }


    /**
     * Mock BackNumber
     */
    private BackNumberEntity mockBackNumber(){
        return BackNumberEntity
                .builder()
                .backNumber(BackNumber.of(100))
                .build();
    }

    /**
     * Mock Member-BackNumber
     */
    private MemberBackNumberEntity mockMemberBackNumber(Member member,BackNumberEntity backNumber){
        return MemberBackNumberEntity.builder()
                .backNumber(backNumber)
                .member(member)
                .build();
    }
}