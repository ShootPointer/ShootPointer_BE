package com.midas.shootpointer.domain.ranking.repository;

import com.midas.shootpointer.domain.ranking.entity.RankingDocument;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RankingRepository extends MongoRepository<RankingDocument,String> {
    //typePeriodKey로 조회
    Optional<RankingDocument> findByTypePeriodKey(String typePeriodKey);
}
