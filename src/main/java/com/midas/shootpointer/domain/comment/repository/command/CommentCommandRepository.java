package com.midas.shootpointer.domain.comment.repository.command;

import com.midas.shootpointer.domain.comment.entity.Comment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CommentCommandRepository extends JpaRepository<Comment, Long> {

}
