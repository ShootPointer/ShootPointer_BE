package com.midas.shootpointer.domain.highlight.entity;

import com.midas.shootpointer.domain.backnumber.entity.BackNumberEntity;
import com.midas.shootpointer.domain.member.entity.Member;
import com.midas.shootpointer.global.common.ErrorCode;
import com.midas.shootpointer.global.entity.BaseEntity;
import com.midas.shootpointer.global.exception.CustomException;
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

    @Column(name = "is_selected")
    @Builder.Default
    private Boolean isSelected=false;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id",nullable = false, columnDefinition = "uuid")
    private Member member;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "back_number_id")
    private BackNumberEntity backNumber;


    /*
    =========== [ 도메인-행위 ] ==============
     */
    public void select(Member actor){
        //유저의 하이라이트 영상이 아닌경우
        if (!actor.getMemberId().equals(member.getMemberId())){
            throw new CustomException(ErrorCode.IS_NOT_CORRECT_MEMBERS_HIGHLIGHT_ID);
        }
        //이미 선택된 하이라이트 영상인 경우
        if (Boolean.TRUE.equals(this.isSelected)){
            throw new CustomException(ErrorCode.EXISTED_SELECTED);
        }
        this.isSelected=true;
    }
}
