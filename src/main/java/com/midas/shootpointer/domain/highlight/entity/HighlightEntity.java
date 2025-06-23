package com.midas.shootpointer.domain.highlight.entity;

import com.midas.shootpointer.domain.backnumber.entity.BackNumber;
import com.midas.shootpointer.domain.backnumber.entity.BackNumberEntity;
import com.midas.shootpointer.domain.member.entity.Member;
import com.midas.shootpointer.global.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Entity
@Slf4j
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Table(name = "higlight")
public class HighlightEntity extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "highlight_id",unique = true,nullable = false)
    private Long highlightId;

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
