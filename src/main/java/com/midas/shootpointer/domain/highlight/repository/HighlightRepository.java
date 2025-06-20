package com.midas.shootpointer.domain.highlight.repository;

import com.midas.shootpointer.domain.highlight.entity.HighlightEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface HighlightRepository extends JpaRepository<HighlightEntity,Long>,HighlightFetchRepository,HighlightSelectRepository {
}
