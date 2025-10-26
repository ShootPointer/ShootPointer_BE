-- =======================
-- Ranking 정보 불러오기
-- =======================
WITH filtered AS (
    SELECT
        h.member_id,
        m.member_name,
        h.two_point_count,
        h.three_point_count
    FROM
        highlight AS h
            JOIN
        member AS m ON h.member_id = m.member_id
    WHERE
        m.is_aggregation_agreed = TRUE
      AND h.is_selected = TRUE
      AND h.created_at >= ?
      AND h.created_at <  ?
)
SELECT
    f.member_name,
    f.member_id,
    SUM(f.two_point_count * 2) AS two_total,
    SUM(f.three_point_count * 3) AS three_total,
    (SUM(f.two_point_count * 2) + SUM(f.three_point_count * 3)) AS total
FROM
    filtered AS f
GROUP BY
    f.member_name, f.member_id
ORDER BY
    total DESC,
    three_total DESC,
    two_total DESC
LIMIT 10;