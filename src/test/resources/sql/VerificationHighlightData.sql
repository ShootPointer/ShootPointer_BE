-- =======================
-- Batch Test용 검증용 쿼리
-- =======================

SELECT
    m.member_name,
    m.member_id,
    SUM(h.two_point_count * 2) as two_total,
    SUM(h.three_point_count * 3) as three_total,
    SUM((h.two_point_count) * 2 +(h.three_point_count) * 3) as total
FROM
    highlight as h
JOIN
    member as m
    ON h.member_id = m.member_id
GROUP BY
    m.member_name, m.member_id
ORDER BY
    total DESC, three_total DESC, two_total DESC
LIMIT
    10;