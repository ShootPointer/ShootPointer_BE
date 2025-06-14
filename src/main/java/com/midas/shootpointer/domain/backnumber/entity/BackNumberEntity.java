package com.midas.shootpointer.domain.backnumber.entity;

import com.midas.shootpointer.global.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Entity
@Slf4j
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "member_back_number")
public class BackNumberEntity extends BaseEntity {
    @Column(name = "meber_back_number_id",unique = true,nullable = false)
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long backNumberId;

    @Embedded
    private BackNumber backNumber;


    //member와 다대다 중간테이블 필요

    //하이라이트 엔티티와 일대다

}
