package com.midas.shootpointer.domain.member.entity;

import com.midas.shootpointer.domain.highlight.entity.HighlightEntity;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Entity
@Data
@NoArgsConstructor
public class Member {

    @Id
    @GeneratedValue
    @Column(name = "memberId")
    private Long member_id;

    @Column(name = "member_name")
    private String username;
    private String email;

    @OneToMany(mappedBy = "HiglightVideo",cascade = CascadeType.ALL,orphanRemoval = true)
    private List<HighlightEntity> highlights=new ArrayList<>();
}
