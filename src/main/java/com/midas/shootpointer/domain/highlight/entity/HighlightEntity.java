package com.midas.shootpointer.domain.highlight.entity;

import com.midas.shootpointer.domain.backnumber.entity.BackNumberEntity;
import com.midas.shootpointer.domain.member.entity.Member;
import com.midas.shootpointer.global.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.annotations.UuidGenerator;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Slf4j
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Table(name = "highlight")
public class HighlightEntity extends BaseEntity {
    @Id
    @UuidGenerator
    @Column(name = "highlight_id",unique = true,nullable = false,columnDefinition = "uuid")
    private UUID highlightId;

    @Column(name = "highlight_url",nullable = false)
    private String highlightURL;

    @Column(name = "highlight_key",nullable = false,columnDefinition = "uuid")
    private UUID highlightKey;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id",nullable = false, columnDefinition = "uuid")
    private Member member;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "back_number_id")
    private BackNumberEntity backNumber;


    @Column(name = "two_point_count",nullable = true)
    @Builder.Default
    private Integer twoPointCount=0;

    @Column(name = "three_point_count",nullable = true)
    @Builder.Default
    private Integer threePointCount=0;

    @Column(name = "video_created_at")
    private LocalDateTime videoCreatedAt;

    @Column(name = "job_id")
    private String jobId;

    /*
    =========== [ 도메인-행위 ] ==============
     */

    //2점 슛 계산
    public int totalTwoPoint(){
        return 2 * this.twoPointCount;
    }

    //3점 슛 계산
    public int totalThreePoint(){
        return 3 * this.threePointCount;
    }
}
