package com.midas.shootpointer.domain.member;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Table(name = "member_test")
public class MemberTest {

    @Id
    @GeneratedValue
    @Column(columnDefinition = "uuid", name = "memberId", nullable = false)
    private UUID id;

    private String memberName;

    public MemberTest(String memberName) {
        this.memberName = memberName;
    }
}
