package com.midas.shootpointer.test;

import com.midas.shootpointer.domain.backnumber.entity.BackNumber;
import com.midas.shootpointer.domain.backnumber.entity.BackNumberEntity;
import com.midas.shootpointer.domain.backnumber.repository.BackNumberRepository;
import com.midas.shootpointer.domain.highlight.entity.HighlightEntity;
import com.midas.shootpointer.domain.highlight.repository.HighlightCommandRepository;
import com.midas.shootpointer.domain.like.entity.LikeEntity;
import com.midas.shootpointer.domain.like.repository.LikeCommandRepository;
import com.midas.shootpointer.domain.member.entity.Member;
import com.midas.shootpointer.domain.member.repository.MemberCommandRepository;
import com.midas.shootpointer.domain.memberbacknumber.entity.MemberBackNumberEntity;
import com.midas.shootpointer.domain.memberbacknumber.repository.MemberBackNumberRepository;
import com.midas.shootpointer.domain.post.entity.HashTag;
import com.midas.shootpointer.domain.post.entity.PostEntity;
import com.midas.shootpointer.domain.post.repository.PostQueryRepository;
import com.midas.shootpointer.test.BasketballPostDataGenerator.PostData;
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
import java.util.*;

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
    //private final PostElasticSearchMapper mapper;
    //private final PostElasticSearchRepository postElasticSearchRepository;
    private final BackNumberRepository backNumberRepository;
    private final MemberBackNumberRepository memberBackNumberRepository;
    private final LikeCommandRepository likeCommandRepository;
    //30개
    private static final String[] videoLink = {
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
        Random random = new Random();
        /**
         * 멤버 생성
         */
        List<Member> memberList = new ArrayList<>();
        for (int i = 0; i < 100; i++) {
            Member member = memberRepository.save(
                    Member.builder()
                            .isAggregationAgreed(true)
                            .username("test" + i)
                            .email("test" + i + "@naver.com")
                            .build()
            );
            memberList.add(member);
        }


        Map<Member, BackNumberEntity> memberBackNumberMap = new HashMap<>();

        for (Member m : memberList) {
            BackNumberEntity bn = backNumberRepository.save(
                    BackNumberEntity.builder()
                            .backNumber(BackNumber.of(random.nextInt(1, 99)))
                            .build()
            );

            memberBackNumberRepository.save(MemberBackNumberEntity.of(m, bn));

            memberBackNumberMap.put(m, bn);
        }

        LocalDateTime now = LocalDateTime.now();
        LocalDateTime threeYearsAgo = now.minusYears(3);

        // 농구 하이라이트 게시물 데이터 생성
        List<PostData> postDataList = BasketballPostDataGenerator.generateRandomPosts(SIZE);

        String sql = "INSERT INTO post (post_id,title, content, hash_tag, highlight_id, member_id,like_cnt,created_at,modified_at) VALUES (?,?, ?, ?, ?, ?, ?, ?, ?)";

        for (int i = 0; i < SIZE; i++) {
            Member member = memberList.get(random.nextInt(memberList.size()));
            BackNumberEntity backNumber = memberBackNumberMap.get(member);
            LocalDateTime randomDateTime;

            if (i < 20) {
                //이번주 데이터 (7일 이내)
                randomDateTime = LocalDateTime.now().minusDays(new Random().nextInt(7));
            } else if (i < 40) {
                //이번달 데이터 (30일 이내)
                randomDateTime = LocalDateTime.now().minusDays(new Random().nextInt(30));
            } else {
                // 기존 3년 랜덤 데이터
                long start = threeYearsAgo.toEpochSecond(ZoneOffset.UTC);
                long end = now.toEpochSecond(ZoneOffset.UTC);
                long randomEpoch = start + (long) (random.nextDouble() * (end - start));
                randomDateTime = LocalDateTime.ofEpochSecond(randomEpoch, 0, ZoneOffset.UTC);
            }
            /*
              Highlight 생성
             */
            String videoUrl = videoLink[random.nextInt(videoLink.length)];
            HighlightEntity highlight = highlightCommandRepository.save(
                    HighlightEntity.builder()
                            .highlightURL("test")
                            .highlightKey(UUID.randomUUID())
                            .highlightURL(videoUrl)
                            .backNumber(backNumber)
                            .threePointCount(random.nextInt(1, 100))
                            .twoPointCount(random.nextInt(1, 100))
                            .member(member)
                            .videoCreatedAt(randomDateTime)
                            .build()
            );


            UUID highlightId = highlight.getHighlightId();
            UUID memberId = member.getMemberId();

            PostData postData = postDataList.get(i);
            //제목
            String title = postData.getTitle();
            //내용
            String content = postData.getContent();
            //좋아요 개수
            Long likeCnt = postData.getLikeCnt();
            //게시물 id
            Long postId = postData.getPostId();


            //UTC 기준으로 하여 LocalDateTime를 long 형태로 변환.
            jdbcTemplate.update(sql,
                    postId, title, content, HashTag.THREE_POINT.name(),
                    highlightId, memberId, likeCnt, randomDateTime, randomDateTime
            );

            PostEntity postEntity = postQueryRepository.findByPostId(postId)
                    .orElseThrow(IllegalStateException::new);


            for (int j = 0; j < likeCnt; j++) {

                Member randomMember = memberList.get(random.nextInt(memberList.size()));

                long startEpoch = randomDateTime.toEpochSecond(ZoneOffset.UTC);
                long endEpoch = now.toEpochSecond(ZoneOffset.UTC);
                long randomLikeEpoch = startEpoch + (long) (random.nextDouble() * (endEpoch - startEpoch));
                LocalDateTime randomLikeTime = LocalDateTime.ofEpochSecond(randomLikeEpoch, 0, ZoneOffset.UTC);

                LikeEntity likeEntity = LikeEntity.builder()
                        .member(randomMember)
                        .post(postEntity)
                        .build();
                likeEntity.setCreatedAt(randomLikeTime);

                likeCommandRepository.save(likeEntity);
            }
            System.out.println("DB - 삽입 완료");
        }


        /**
         * Elastic 배치 쿼리
         */


        List<PostEntity> repositoryAll = postQueryRepository.findAllWithMemberAndHighlight();

        // PostEntity → PostDocument 변환
        /*List<PostDocument> docs = repositoryAll.stream()
                .map(mapper::entityToDoc)
                .toList();

        postElasticSearchRepository.saveAll(docs);
        System.out.println("ES - 삽입 완료");*/
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
