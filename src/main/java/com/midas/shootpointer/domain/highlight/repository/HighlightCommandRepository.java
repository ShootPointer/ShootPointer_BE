package com.midas.shootpointer.domain.highlight.repository;

import com.midas.shootpointer.domain.highlight.entity.HighlightEntity;
import com.midas.shootpointer.domain.member.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface HighlightCommandRepository extends JpaRepository<HighlightEntity, UUID> {
}
