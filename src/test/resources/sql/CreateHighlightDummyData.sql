-- =======================
-- Batch Test용 Dummy Data
-- =======================

-- 1.기존 데이터 삭제
TRUNCATE TABLE highlight CASCADE;
TRUNCATE TABLE member_back_number CASCADE;
TRUNCATE TABLE back_number CASCADE;
TRUNCATE TABLE member CASCADE;


-- 2.유저 20명 생성
CREATE EXTENSION IF NOT EXISTS "pgcrypto";

DO
$$
    DECLARE
        i     INT;
        agree BOOLEAN;
    BEGIN
        FOR i IN 1..20
            LOOP
                IF (i % 3 = 0) THEN
                    agree := TRUE;
                ELSE
                    agree := FALSE;
                END IF;

                INSERT INTO member (member_id,
                                    email,
                                    member_name,
                                    is_aggregation_agreed,
                                    created_at,
                                    modified_at)
                VALUES (gen_random_uuid(),
                        format('test_%s@naver.com', i),
                        format('테스터%s', i),
                        agree,
                        NOW(),
                        NOW());
            END LOOP;
    END
$$;

-- 3. BackNumber 생성 (1~30번 랜덤)
DO
$$
    DECLARE
        i INT;
    BEGIN
        FOR i IN 1..30
            LOOP
                INSERT INTO back_number (back_number_id, back_number_value)
                VALUES (i, i);
            END LOOP;
    END
$$;

-- 4. MemberBackNumber 생성 및 매핑
DO
$$
    DECLARE
        m RECORD;
        b RECORD;
    BEGIN
        FOR m IN SELECT member_id FROM member
            LOOP
                SELECT * INTO b FROM back_number ORDER BY random() LIMIT 1;
                INSERT INTO member_back_number (member_id, back_number_id)
                VALUES (m.member_id, b.back_number_id);
            END LOOP;
    END
$$;


-- 5. Highlight 생성
DO
$$
    DECLARE
        m          RECORD;
        b          RECORD;
        i          INT;
        created_at TIMESTAMP;
        video_urls TEXT[] := ARRAY [
            'https://video-previews.elements.envatousercontent.com/09664892-2b57-461c-8055-eec6dc4b03f1/watermarked_preview/watermarked_preview.mp4',
            'https://video-previews.elements.envatousercontent.com/8c037672-3c88-4e30-a270-32401c0b3426/watermarked_preview/watermarked_preview.mp4',
            'https://video-previews.elements.envatousercontent.com/68c3dbdb-dfed-4263-be19-6e7b6f993ffd/watermarked_preview/watermarked_preview.mp4',
            'https://video-previews.elements.envatousercontent.com/b89cfa86-d8ad-4c46-806e-c8eec174b255/watermarked_preview/watermarked_preview.mp4',
            'https://video-previews.elements.envatousercontent.com/82fde809-6805-445a-94e5-4c0a478e732f/watermarked_preview/watermarked_preview.mp4',
            'https://video-previews.elements.envatousercontent.com/77b39e58-b4a2-40bd-9862-3daac799d80b/watermarked_preview/watermarked_preview.mp4',
            'https://video-previews.elements.envatousercontent.com/b0e950da-0a79-483f-9804-b5cea4f6d195/watermarked_preview/watermarked_preview.mp4',
            'https://video-previews.elements.envatousercontent.com/adec4248-7da9-40e6-bab5-ed8c06ed16f6/watermarked_preview/watermarked_preview.mp4',
            'https://video-previews.elements.envatousercontent.com/cbb4ddfa-909d-4798-9846-78287ddd2ec5/watermarked_preview/watermarked_preview.mp4',
            'https://video-previews.elements.envatousercontent.com/f95259b9-ae81-471c-8f22-03513c4e98c7/watermarked_preview/watermarked_preview.mp4',
            'https://video-previews.elements.envatousercontent.com/596bf1d5-19f7-4698-b5ec-1a58061b542c/watermarked_preview/watermarked_preview.mp4',
            'https://video-previews.elements.envatousercontent.com/ecac03b1-dc83-4e96-9c15-3847c3ec95fe/watermarked_preview/watermarked_preview.mp4',
            'https://video-previews.elements.envatousercontent.com/5c866122-ec42-455b-9bef-b3791fe57471/watermarked_preview/watermarked_preview.mp4',
            'https://video-previews.elements.envatousercontent.com/3bbc7c7f-0613-4c70-8b36-c4c5d1a4b920/watermarked_preview/watermarked_preview.mp4',
            'https://video-previews.elements.envatousercontent.com/18ad1c3f-f58f-4c9a-a7d2-6ffcc0b75498/watermarked_preview/watermarked_preview.mp4',
            'https://video-previews.elements.envatousercontent.com/89d7b3a7-45bb-4737-a30d-a69f564f1c07/watermarked_preview/watermarked_preview.mp4',
            'https://video-previews.elements.envatousercontent.com/28dc12f5-20b8-4039-ac4e-1daa989618c0/watermarked_preview/watermarked_preview.mp4',
            'https://video-previews.elements.envatousercontent.com/4d322fdc-713c-4bde-bdaa-28d78d97e5fd/watermarked_preview/watermarked_preview.mp4',
            'https://video-previews.elements.envatousercontent.com/8f3c2327-efde-466b-b1b8-2b2ada5f6583/watermarked_preview/watermarked_preview.mp4',
            'https://video-previews.elements.envatousercontent.com/3d604467-076d-4a68-a0af-92a965734161/watermarked_preview/watermarked_preview.mp4',
            'https://video-previews.elements.envatousercontent.com/ff4edec5-fc82-4e3e-848a-7ae6119c869f/watermarked_preview/watermarked_preview.mp4'
            ];
    BEGIN
        FOR m IN SELECT member_id FROM member
            LOOP
                FOR i IN 1..50
                    LOOP
                        created_at := NOW() - (random() * INTERVAL '100 days');

                        INSERT INTO highlight(highlight_id,
                                              highlight_key,
                                              highlight_url,
                                              member_id,
                                              two_point_count,
                                              three_point_count,
                                              is_selected,
                                              created_at,
                                              modified_at)
                        VALUES (gen_random_uuid(),
                                gen_random_uuid(),
                                video_urls[ceil(random() * array_length(video_urls, 1))],
                                m.member_id,
                                FLOOR(random() * 20)::INT, -- 0 ~ 19개
                                FLOOR(random() * 15)::INT, --0~14개
                                CASE WHEN random() > 0.15 THEN TRUE ELSE FALSE END,
                                created_at,
                                created_at);
                    END LOOP;
            END LOOP;
    END
$$;

