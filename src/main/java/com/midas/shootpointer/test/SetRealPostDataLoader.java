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
import com.midas.shootpointer.domain.post.entity.HashTag;
import com.midas.shootpointer.domain.post.entity.PostDocument;
import com.midas.shootpointer.domain.post.entity.PostEntity;
import com.midas.shootpointer.domain.post.mapper.PostElasticSearchMapper;
import com.midas.shootpointer.domain.post.repository.PostElasticSearchRepository;
import com.midas.shootpointer.domain.post.repository.PostQueryRepository;
import lombok.RequiredArgsConstructor;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.UUID;

@Component
@Profile("test-real-data")
@RequiredArgsConstructor
/**
 * 실제 게시물 데이터를 DB와 ElasticSearch에 저장
 */
public class SetRealPostDataLoader implements CommandLineRunner {
    private final static int SIZE = 100;

    private final JdbcTemplate jdbcTemplate;
    private final MemberCommandRepository memberRepository;
    private final HighlightCommandRepository highlightCommandRepository;
    private final PostQueryRepository postQueryRepository;
    private final PostElasticSearchMapper mapper;
    private final PostElasticSearchRepository postElasticSearchRepository;
    private final BackNumberRepository backNumberRepository;
    private final MemberBackNumberRepository memberBackNumberRepository;
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
    /**
     * Callback used to run the bean.
     *
     * @param args incoming main method arguments
     * @throws Exception on error
     */
    @Override
    public void run(String... args) throws Exception {
        List<PostEntity> postEntities = new ArrayList<>();
        Member member = memberRepository.save(Member.builder()
                .email("test@naver.com")
                .username("test")
                .build()
        );

        BackNumberEntity backNumberEntity=
                BackNumberEntity.builder()
                        .backNumber(BackNumber.of(25))
                        .build();
        backNumberEntity=backNumberRepository.save(backNumberEntity);

        //중간 테이블 생성 및 저장
        MemberBackNumberEntity memberBackNumberEntity=MemberBackNumberEntity.of(member,backNumberEntity);
        memberBackNumberRepository.save(memberBackNumberEntity);

        Random random = new Random();
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime threeYearsAgo = now.minusYears(3);



        //Json parsing한 데이터
        List<Object[]> dataList = readJson();

        String sql = "INSERT INTO post (post_id,title, content, hash_tag, highlight_id, member_id,like_cnt,created_at,modified_at) VALUES (?,?, ?, ?, ?, ?, ?, ?, ?)";
        List<Object[]> batchArgs = new ArrayList<>();

        for (int i = 0; i < SIZE; i++) {
            /*
              Highlight 생성
             */
            String videoUrl=videoLink[random.nextInt(videoLink.length)];
            HighlightEntity highlight = highlightCommandRepository.save(
                    HighlightEntity.builder()
                            .highlightURL("test")
                            .highlightKey(UUID.randomUUID())
                            .highlightURL(videoUrl)
                            .isSelected(true)
                            .backNumber(backNumberEntity)
                            .threePointCount(random.nextInt(1,100))
                            .twoPointCount(random.nextInt(1,100))
                            .member(member)
                            .build()
            );


            UUID highlightId = highlight.getHighlightId();
            UUID memberId = member.getMemberId();

            //제목
            String title = dataList.get(i)[0].toString();
            //내용
            String content = dataList.get(i)[1].toString();
            //좋아요 개수
            Long likeCnt = (Long) dataList.get(i)[2];
            //게시물 id
            Long postId = (Long) dataList.get(i)[3];

            // 랜덤 날짜
            long start = threeYearsAgo.toEpochSecond(ZoneOffset.UTC);
            long end = now.toEpochSecond(ZoneOffset.UTC);
            long randomEpoch = start + (long) (random.nextDouble() * (end - start));
            //UTC 기준으로 하여 LocalDateTime를 long 형태로 변환.
            LocalDateTime randomDateTime = LocalDateTime.ofEpochSecond(randomEpoch, 0, ZoneOffset.UTC);

            batchArgs.add(new Object[]{postId, title, content, HashTag.THREE_POINT.name(), highlightId, memberId, likeCnt, randomDateTime, randomDateTime});

            if (i > 0) {
                jdbcTemplate.batchUpdate(sql, batchArgs);
                batchArgs.clear();
                System.out.println("DB - 삽입 완료");
            }
        }

        if (!batchArgs.isEmpty()) {
            jdbcTemplate.batchUpdate(sql, batchArgs);
        }

        /**
         * Elastic 배치 쿼리
         */


        List<PostEntity> repositoryAll = postQueryRepository.findAllWithMemberAndHighlight();

        // PostEntity → PostDocument 변환
        List<PostDocument> docs = repositoryAll.stream()
                .map(mapper::entityToDoc)
                .toList();

        postElasticSearchRepository.saveAll(docs);
        System.out.println("ES - 삽입 완료");


    }


    /**
     * Json file Reader
     */
    private static List<Object[]> readJson() {
        JSONParser parser = new JSONParser();
        List<Object[]> dataList = new ArrayList<>();
        try {
            ClassPathResource resource = new ClassPathResource("documents_clean.json");
            try (Reader reader = new InputStreamReader(resource.getInputStream())) {
                JSONArray dataArray = (JSONArray) parser.parse(reader);

                for (Object obj : dataArray) {
                    JSONObject element = (JSONObject) obj;
                    String title = (String) element.get("title");
                    String content = (String) element.get("content");
                    Long likeCnt = (Long) element.get("likeCnt");
                    Long id = (Long) element.get("id");

                    dataList.add(new Object[]{title, content, likeCnt, id});
                }
            }
        } catch (IOException e) {
            throw new RuntimeException("파일 읽기 실패", e);
        } catch (ParseException e) {
            throw new RuntimeException("JSON 파싱 실패", e);
        }
        return dataList;
    }


}

