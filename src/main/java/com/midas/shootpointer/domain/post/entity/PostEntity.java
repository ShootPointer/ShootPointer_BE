package com.midas.shootpointer.domain.post.entity;

import com.midas.shootpointer.domain.member.entity.Member;
import com.midas.shootpointer.global.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Table(name = "post")
@AllArgsConstructor
@RequiredArgsConstructor
@Entity
@Builder
@Getter
public class PostEntity extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "post_id",unique = true,nullable = false)
    private Long postId;

    @Column(name = "title",length = 20)
    private String title;

    @Column(name = "content",columnDefinition = "TEXT",length = 1000)
    private String content;

    @Enumerated(EnumType.STRING)
    @Column(name = "hash_tag")
    private HashTag hashTag;

    @ManyToOne(fetch =FetchType.LAZY)
    @JoinColumn(name = "member_id",nullable = false)
    private Member member;

}
