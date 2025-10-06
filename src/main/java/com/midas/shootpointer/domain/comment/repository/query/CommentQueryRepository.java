package com.midas.shootpointer.domain.comment.repository.query;

import com.midas.shootpointer.domain.comment.entity.Comment;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface CommentQueryRepository extends JpaRepository<Comment, Long> {
	
	@Query("SELECT c FROM Comment c " +
		"JOIN FETCH c.member " +
		"WHERE c.post.postId = :postId " +
		"ORDER BY c.createdAt DESC")
	List<Comment> findAllByPostIdOrderByCreatedAtDesc(@Param("postId") Long postId);
	
	Optional<Comment> findCommentByCommentId(Long commentId);
}
