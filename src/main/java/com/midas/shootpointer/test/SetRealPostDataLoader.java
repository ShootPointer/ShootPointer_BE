package com.midas.shootpointer.test;

import com.midas.shootpointer.domain.highlight.entity.HighlightEntity;
import com.midas.shootpointer.domain.highlight.repository.HighlightCommandRepository;
import com.midas.shootpointer.domain.member.entity.Member;
import com.midas.shootpointer.domain.member.repository.MemberCommandRepository;
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

        HighlightEntity highlight = highlightCommandRepository.save(
                HighlightEntity.builder()
                        .highlightURL("test")
                        .highlightKey(UUID.randomUUID())
                        .member(member)
                        .build()
        );


        UUID highlightId = highlight.getHighlightId();
        UUID memberId = member.getMemberId();
        Random random = new Random();
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime threeYearsAgo = now.minusYears(3);

        //Json parsing한 데이터
        List<Object[]> dataList = readJson();

        String sql = "INSERT INTO post (post_id,title, content, hash_tag, highlight_id, member_id,like_cnt,created_at,modified_at) VALUES (?,?, ?, ?, ?, ?, ?, ?, ?)";
        List<Object[]> batchArgs = new ArrayList<>();

        for (int i = 0; i < SIZE; i++) {
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

            batchArgs.add(new Object[]{postId, title, content, HashTag.TREE_POINT.name(), highlightId, memberId, likeCnt, randomDateTime, randomDateTime});

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

