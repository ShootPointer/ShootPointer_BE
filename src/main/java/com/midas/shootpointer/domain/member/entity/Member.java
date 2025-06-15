package com.midas.shootpointer.domain.member.entity;

import com.midas.shootpointer.domain.highlight.entity.HighlightEntity;
import jakarta.persistence.*;
import com.midas.shootpointer.global.entity.BaseTimeEntity;
import com.midas.shootpointer.global.util.encrypt.EncryptConverter;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Entity
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Member extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "member_id")
    private Long memberId;

    @Column(name = "member_name")
    @Convert(converter = EncryptConverter.class)
    private String username;

    @Convert(converter = EncryptConverter.class)
    private String email;

    @OneToMany(mappedBy = "HiglightVideo",cascade = CascadeType.ALL,orphanRemoval = true)
    private List<HighlightEntity> highlights=new ArrayList<>();
}
