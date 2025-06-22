package com.midas.shootpointer.domain.member.entity;

import com.midas.shootpointer.domain.highlight.entity.HighlightEntity;
import com.midas.shootpointer.domain.memberbacknumber.entity.MemberBackNumberEntity;
import jakarta.persistence.*;
import com.midas.shootpointer.global.entity.BaseTimeEntity;
import com.midas.shootpointer.global.util.encrypt.EncryptConverter;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Member extends BaseTimeEntity {

    @Id
    @GeneratedValue
    @Column(name = "member_id", columnDefinition = "uuid")
    private UUID memberId;

    @Column(name = "member_name")
    @Convert(converter = EncryptConverter.class)
    private String username;

    @Convert(converter = EncryptConverter.class)
    private String email;

    @OneToMany(mappedBy = "member",cascade = CascadeType.ALL,orphanRemoval = true)
    private List<HighlightEntity> highlights=new ArrayList<>();

    @OneToMany(mappedBy = "member",cascade = CascadeType.ALL,orphanRemoval = true)
    private List<MemberBackNumberEntity> memberBackNumbers=new ArrayList<>();
}
