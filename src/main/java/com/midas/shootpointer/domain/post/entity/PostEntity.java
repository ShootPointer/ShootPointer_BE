package com.midas.shootpointer.domain.post.entity;

import com.midas.shootpointer.domain.member.entity.Member;
import com.midas.shootpointer.global.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.RequiredArgsConstructor;

@Table(name = "post")
@AllArgsConstructor
@RequiredArgsConstructor
@Entity
@Builder
public class PostEntity extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "post_id",unique = true,nullable = false)
    private Long postId;

    @Column(name = "title",nullable = false,length = 20)
    private String title;

    @Column(name = "description",columnDefinition = "TEXT",length = 1000)
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(name = "hash_tag")
    private HashTag hashTag;

    @ManyToOne(fetch =FetchType.LAZY)
    @JoinColumn(name = "member_id",nullable = false)
    private Member member;

}
