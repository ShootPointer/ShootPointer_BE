package com.midas.shootpointer.domain.memberbacknumber.entity;

import com.midas.shootpointer.domain.backnumber.entity.BackNumberEntity;
import com.midas.shootpointer.domain.member.entity.Member;
import com.midas.shootpointer.global.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "member_back_number")
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MemberBackNumberEntity extends BaseEntity {
    @Id
    @Column(name = "member_back_number_id",unique = true,nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long memberBackNumberId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id",nullable = false, columnDefinition = "UUID")
    private Member member;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "back_number_id",nullable = false)
    private BackNumberEntity backNumber;


    public static MemberBackNumberEntity of(Member member,BackNumberEntity backNumber){
        return new MemberBackNumberEntity(null,member,backNumber);
    }
}
