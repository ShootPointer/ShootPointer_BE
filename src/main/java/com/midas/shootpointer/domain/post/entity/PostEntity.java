package com.midas.shootpointer.domain.post.entity;

import com.midas.shootpointer.domain.highlight.entity.HighlightEntity;
import com.midas.shootpointer.domain.member.entity.Member;
import com.midas.shootpointer.global.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.SQLRestriction;

import java.util.concurrent.atomic.AtomicInteger;

@Table(name = "post")
@AllArgsConstructor
@RequiredArgsConstructor
@Entity
@Builder
@Getter
@SQLRestriction("is_deleted = false")
public class PostEntity extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "post_id",unique = true,nullable = false)
    private Long postId;

    @Column(name = "title",length = 20,nullable = false)
    private String title;

    @Column(name = "content",columnDefinition = "TEXT",length = 1000)
    private String content;

    @Enumerated(EnumType.STRING)
    @Column(name = "hash_tag")
    private HashTag hashTag;


    @Column(name = "like_cnt")
    @Builder.Default
    @Setter
    private Integer likeCnt=0;

    /**
     * 원시적 변수
     */
    @Transient
    @Setter
    private AtomicInteger atomicLikeCnt=new AtomicInteger(0);


    @ManyToOne(fetch =FetchType.LAZY)
    @JoinColumn(name = "member_id",nullable = false)
    private Member member;

    @Setter
    @OneToOne
    @JoinColumn(name = "highlight_id",unique = true)
    private HighlightEntity highlight;

    public void update(String title,String content,HashTag hashTag,HighlightEntity highlight){
        this.title=title;
        this.content=content;
        this.hashTag=hashTag;
        this.highlight=highlight;
    }


    public void deleteLike(){
        if(this.likeCnt>0){
            this.likeCnt--;
        }else{
            this.likeCnt=0;
        }
    }

    public int increaseAtomicLikeCnt(){
        return this.atomicLikeCnt.getAndIncrement();
    }

}