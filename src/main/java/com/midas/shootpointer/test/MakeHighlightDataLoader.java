package com.midas.shootpointer.test;

import com.midas.shootpointer.domain.backnumber.entity.BackNumber;
import com.midas.shootpointer.domain.backnumber.entity.BackNumberEntity;
import com.midas.shootpointer.domain.backnumber.repository.BackNumberRepository;
import com.midas.shootpointer.domain.highlight.entity.HighlightEntity;
import com.midas.shootpointer.domain.highlight.repository.HighlightCommandRepository;
import com.midas.shootpointer.domain.member.entity.Member;
import com.midas.shootpointer.domain.member.repository.MemberCommandRepository;
import com.midas.shootpointer.domain.memberbacknumber.entity.MemberBackNumberEntity;
import com.midas.shootpointer.domain.memberbacknumber.repository.MemberBackNumberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.util.Random;
import java.util.UUID;

@Component
@Profile("test-highlight-data")
@RequiredArgsConstructor
/*
 * 실제 하이라이트 데이터 DB에 저장
 */

public class MakeHighlightDataLoader implements CommandLineRunner {
    //30개
    private static final String[] videoLink={
            "https://video-previews.elements.envatousercontent.com/09664892-2b57-461c-8055-eec6dc4b03f1/watermarked_preview/watermarked_preview.mp4",
            "https://video-previews.elements.envatousercontent.com/8c037672-3c88-4e30-a270-32401c0b3426/watermarked_preview/watermarked_preview.mp4",
            "https://video-previews.elements.envatousercontent.com/68c3dbdb-dfed-4263-be19-6e7b6f993ffd/watermarked_preview/watermarked_preview.mp4",
            "https://video-previews.elements.envatousercontent.com/b89cfa86-d8ad-4c46-806e-c8eec174b255/watermarked_preview/watermarked_preview.mp4",
            "https://video-previews.elements.envatousercontent.com/82fde809-6805-445a-94e5-4c0a478e732f/watermarked_preview/watermarked_preview.mp4",
            "https://video-previews.elements.envatousercontent.com/77b39e58-b4a2-40bd-9862-3daac799d80b/watermarked_preview/watermarked_preview.mp4",
            "https://video-previews.elements.envatousercontent.com/b0e950da-0a79-483f-9804-b5cea4f6d195/watermarked_preview/watermarked_preview.mp4",
            "https://video-previews.elements.envatousercontent.com/b0e950da-0a79-483f-9804-b5cea4f6d195/watermarked_preview/watermarked_preview.mp4",
            "https://video-previews.elements.envatousercontent.com/adec4248-7da9-40e6-bab5-ed8c06ed16f6/watermarked_preview/watermarked_preview.mp4",
            "https://video-previews.elements.envatousercontent.com/cbb4ddfa-909d-4798-9846-78287ddd2ec5/watermarked_preview/watermarked_preview.mp4",
            "https://video-previews.elements.envatousercontent.com/f95259b9-ae81-471c-8f22-03513c4e98c7/watermarked_preview/watermarked_preview.mp4",
            "https://video-previews.elements.envatousercontent.com/596bf1d5-19f7-4698-b5ec-1a58061b542c/watermarked_preview/watermarked_preview.mp4",
            "https://video-previews.elements.envatousercontent.com/ecac03b1-dc83-4e96-9c15-3847c3ec95fe/watermarked_preview/watermarked_preview.mp4",
            "https://video-previews.elements.envatousercontent.com/5c866122-ec42-455b-9bef-b3791fe57471/watermarked_preview/watermarked_preview.mp4",
            "https://video-previews.elements.envatousercontent.com/3bbc7c7f-0613-4c70-8b36-c4c5d1a4b920/watermarked_preview/watermarked_preview.mp4",
            "https://video-previews.elements.envatousercontent.com/18ad1c3f-f58f-4c9a-a7d2-6ffcc0b75498/watermarked_preview/watermarked_preview.mp4",
            "https://video-previews.elements.envatousercontent.com/89d7b3a7-45bb-4737-a30d-a69f564f1c07/watermarked_preview/watermarked_preview.mp4",
            "https://video-previews.elements.envatousercontent.com/28dc12f5-20b8-4039-ac4e-1daa989618c0/watermarked_preview/watermarked_preview.mp4",
            "https://video-previews.elements.envatousercontent.com/f95259b9-ae81-471c-8f22-03513c4e98c7/watermarked_preview/watermarked_preview.mp4",
            "https://video-previews.elements.envatousercontent.com/4d322fdc-713c-4bde-bdaa-28d78d97e5fd/watermarked_preview/watermarked_preview.mp4",
            "https://video-previews.elements.envatousercontent.com/8f3c2327-efde-466b-b1b8-2b2ada5f6583/watermarked_preview/watermarked_preview.mp4",
            "https://video-previews.elements.envatousercontent.com/3d604467-076d-4a68-a0af-92a965734161/watermarked_preview/watermarked_preview.mp4",
            "https://video-previews.elements.envatousercontent.com/ff4edec5-fc82-4e3e-848a-7ae6119c869f/watermarked_preview/watermarked_preview.mp4",
            "https://video-previews.elements.envatousercontent.com/89d7b3a7-45bb-4737-a30d-a69f564f1c07/watermarked_preview/watermarked_preview.mp4",
            "https://video-previews.elements.envatousercontent.com/28dc12f5-20b8-4039-ac4e-1daa989618c0/watermarked_preview/watermarked_preview.mp4",
            "https://video-previews.elements.envatousercontent.com/f95259b9-ae81-471c-8f22-03513c4e98c7/watermarked_preview/watermarked_preview.mp4",
            "https://video-previews.elements.envatousercontent.com/4d322fdc-713c-4bde-bdaa-28d78d97e5fd/watermarked_preview/watermarked_preview.mp4",
            "https://video-previews.elements.envatousercontent.com/8f3c2327-efde-466b-b1b8-2b2ada5f6583/watermarked_preview/watermarked_preview.mp4",
            "https://video-previews.elements.envatousercontent.com/3d604467-076d-4a68-a0af-92a965734161/watermarked_preview/watermarked_preview.mp4",
            "https://video-previews.elements.envatousercontent.com/ff4edec5-fc82-4e3e-848a-7ae6119c869f/watermarked_preview/watermarked_preview.mp4"
    };


    private final MemberCommandRepository memberCommandRepository;
    private final HighlightCommandRepository highlightCommandRepository;
    private final BackNumberRepository backNumberRepository;
    private final MemberBackNumberRepository memberBackNumberRepository;

    @Override
    public void run(String... args) throws Exception {
        Random random=new Random();

        for (int i=0;i<10;i++){
            Member member=Member.builder()
                    .email("test" + UUID.randomUUID().toString().substring(0,5)+"@naver.com")
                    .username("user"+random.nextInt(10))
                    .build();
            member=memberCommandRepository.save(member);

            //0~99번 랜덤 매칭
            BackNumberEntity backNumberEntity=
                    BackNumberEntity.builder()
                            .backNumber(BackNumber.of(Math.max(1,random.nextInt(100))))
                            .build();
            backNumberEntity=backNumberRepository.save(backNumberEntity);

            //중간 테이블 생성 및 저장
            MemberBackNumberEntity memberBackNumberEntity=MemberBackNumberEntity.of(member,backNumberEntity);
            memberBackNumberRepository.save(memberBackNumberEntity);

            for (int j=i;j<3;j++){
                HighlightEntity highlight=HighlightEntity.builder()
                        .highlightURL(videoLink[j])
                        .twoPointCount(random.nextInt(20))
                        .threePointCount(random.nextInt(15))
                        .highlightKey(UUID.randomUUID())
                        .member(member)
                        .backNumber(backNumberEntity)
                        .build();
                highlightCommandRepository.save(highlight);
            }
        }



    }
}
