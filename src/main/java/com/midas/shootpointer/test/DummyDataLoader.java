package com.midas.shootpointer.test;

import com.midas.shootpointer.domain.highlight.entity.HighlightEntity;
import com.midas.shootpointer.domain.highlight.repository.HighlightCommandRepository;
import com.midas.shootpointer.domain.member.entity.Member;
import com.midas.shootpointer.domain.member.repository.MemberCommandRepository;
import com.midas.shootpointer.domain.post.elasticsearch.PostDocument;
import com.midas.shootpointer.domain.post.elasticsearch.PostElasticSearchRepository;
import com.midas.shootpointer.domain.post.entity.HashTag;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.UUID;

@Component
@Profile("testdata")
@RequiredArgsConstructor
public class DummyDataLoader implements CommandLineRunner {
    private final JdbcTemplate jdbcTemplate;
    private final MakeRandomWord makeRandomWord;
    private final MemberCommandRepository memberRepository;
    private final HighlightCommandRepository highlightCommandRepository;
    private final PostElasticSearchRepository postElasticSearchRepository;

    private final int batchSize=10_000;
    private final int insertSize=10_000_000;

    @Override
    public void run(String... args) throws Exception {
        Member member=memberRepository.save(Member.builder()
                .email("test@naver.com")
                .username("test")
                .build()
        );

        HighlightEntity highlight=highlightCommandRepository.save(
                HighlightEntity.builder()
                        .highlightURL("test")
                        .highlightKey(UUID.randomUUID())
                        .member(member)
                        .build()
        );


        UUID highlightId=highlight.getHighlightId();
        UUID memberId=member.getMemberId();

        Random random=new Random();
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime threeYearsAgo = now.minusYears(3);

        String sql="INSERT INTO post (title, content, hash_tag, highlight_id, member_id,like_cnt,created_at,modified_at) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        List<Object[]> batchArgs=new ArrayList<>();
        List<PostDocument> esBatch=new ArrayList<>(); //ElasticSearch용


        for (int i=0;i<insertSize;i++){
            //제목
            String title=makeRandomWord.generateTitle();

            //내용
            String content=makeRandomWord.generateContent();

            //좋아요 개수
            long likeCnt = random.nextInt(100_000) + 1; // 1 ~ 100000

            // 랜덤 날짜
            long start = threeYearsAgo.toEpochSecond(ZoneOffset.UTC);
            long end = now.toEpochSecond(ZoneOffset.UTC);
            long randomEpoch = start + (long) (random.nextDouble() * (end - start));
            //UTC 기준으로 하여 LocalDateTime를 long 형태로 변환.
            LocalDateTime randomDateTime = LocalDateTime.ofEpochSecond(randomEpoch, 0, ZoneOffset.UTC);

            batchArgs.add(new Object[]{title,content, HashTag.TREE_POINT.name(),highlightId,memberId,likeCnt,randomDateTime,randomDateTime});

            //ElasticSearch용 Batch
            esBatch.add(PostDocument.builder()
                            .title(title)
                            .memberName(member.getUsername())
                            .hashTag(HashTag.TREE_POINT.getName())
                            .content(content)
                            .likeCnt(likeCnt)
                            .createdAt(randomDateTime)
                            .modifiedAt(randomDateTime)
                            .build()
            );
            if (i > 0 && i%batchSize == 0){
                jdbcTemplate.batchUpdate(sql,batchArgs);
                batchArgs.clear();

                postElasticSearchRepository.saveAll(esBatch);
                System.out.println(i+"건 삽입 완료");
            }

        }

        if (!batchArgs.isEmpty()){
            jdbcTemplate.batchUpdate(sql,batchArgs);
        }

    }
}
