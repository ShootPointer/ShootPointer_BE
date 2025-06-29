package com.midas.shootpointer.domain.backnumber.entity;

import com.midas.shootpointer.domain.highlight.entity.HighlightEntity;
import com.midas.shootpointer.domain.memberbacknumber.entity.MemberBackNumberEntity;
import com.midas.shootpointer.global.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;

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


   @OneToMany(mappedBy = "backNumber",cascade = CascadeType.ALL,orphanRemoval = true)
    private List<HighlightEntity> highlights=new ArrayList<>();

   @OneToMany(mappedBy ="backNumber",cascade = CascadeType.ALL,orphanRemoval = true)
    private List<MemberBackNumberEntity> memberBackNumbers=new ArrayList<>();

}
