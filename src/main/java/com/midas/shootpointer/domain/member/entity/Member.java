package com.midas.shootpointer.domain.member.entity;

import com.midas.shootpointer.domain.highlight.entity.HighlightEntity;
import com.midas.shootpointer.domain.memberbacknumber.entity.MemberBackNumberEntity;
import com.midas.shootpointer.global.entity.BaseEntity;
import jakarta.persistence.*;
import com.midas.shootpointer.global.entity.BaseTimeEntity;
import com.midas.shootpointer.global.util.encrypt.EncryptConverter;
import lombok.*;
import org.hibernate.annotations.UuidGenerator;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Table(name = "member")
public class Member extends BaseEntity {

    @Id
    @UuidGenerator
    @Column(name = "member_id", columnDefinition = "uuid")
    private UUID memberId;

    @Column(name = "member_name")
    @Convert(converter = EncryptConverter.class)
    private String username;

    @Convert(converter = EncryptConverter.class)
    private String email;

    @OneToMany(mappedBy = "member",cascade = CascadeType.ALL,orphanRemoval = true)
    @Builder.Default
    private List<HighlightEntity> highlights=new ArrayList<>();

    @OneToMany(mappedBy = "member",cascade = CascadeType.ALL,orphanRemoval = true)
    @Builder.Default
    private List<MemberBackNumberEntity> memberBackNumbers=new ArrayList<>();
}
