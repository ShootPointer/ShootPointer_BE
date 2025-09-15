package com.midas.shootpointer.domain.backnumber.entity;

import com.midas.shootpointer.global.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Entity
@Slf4j
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Table(name = "back_number")
@Getter
public class BackNumberEntity extends BaseEntity {
    @Column(name = "back_number_id",unique = true,nullable = false)
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long backNumberId;

    @Embedded
    private BackNumber backNumber;
}
