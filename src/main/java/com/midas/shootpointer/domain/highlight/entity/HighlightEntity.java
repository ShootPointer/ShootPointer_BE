package com.midas.shootpointer.domain.highlight.entity;

import com.midas.shootpointer.domain.backnumber.entity.BackNumber;
import com.midas.shootpointer.domain.backnumber.entity.BackNumberEntity;
import com.midas.shootpointer.domain.member.entity.Member;
import com.midas.shootpointer.global.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.annotations.UuidGenerator;

import java.util.UUID;

@Entity
@Slf4j
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Table(name = "higlight_video")
public class HighlightEntity extends BaseEntity {
    @Id
    @UuidGenerator
    @Column(name = "highlight_video_id",unique = true,nullable = false,columnDefinition = "BINARY(16)")
    private UUID highlightId;

    @Column(name = "highlight_url",nullable = false)
    private String highlightURL;

    @Column(name = "is_selected")
    private Boolean isSelected=false;

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "member_id",nullable = false, columnDefinition = "uuid")
    private Member member;

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "back_number_id",nullable = false)
    private BackNumberEntity backNumber;

    public void select(){
        this.isSelected=true;
    }
}
