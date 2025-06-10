package com.midas.shootpointer.domain.member.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import lombok.Data;
import lombok.NoArgsConstructor;

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
}
