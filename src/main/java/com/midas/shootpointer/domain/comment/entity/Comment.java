package com.midas.shootpointer.domain.comment.entity;

import com.midas.shootpointer.domain.member.entity.Member;
import com.midas.shootpointer.domain.post.entity.PostEntity;
import com.midas.shootpointer.global.entity.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.hibernate.annotations.SQLRestriction;

@Entity
@RequiredArgsConstructor
@AllArgsConstructor
@Table(name = "comment",
	indexes = {@Index(name = "idx_comment_post_created", columnList = "post_id, created_at DESC")}
)
@SQLRestriction("is_deleted = false")
@Getter
@Builder
public class Comment extends BaseEntity {

	@Id @GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "comment_id", unique = true, nullable = false)
	private Long commentId;
	
	@Column(name = "content", columnDefinition = "TEXT", length = 500, nullable = false)
	private String content;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "member_id", nullable = false)
	private Member member;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "post_id", nullable = false)
	private PostEntity post;
	
}
