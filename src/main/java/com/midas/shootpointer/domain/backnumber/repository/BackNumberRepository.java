package com.midas.shootpointer.domain.backnumber.repository;

import com.midas.shootpointer.domain.backnumber.entity.BackNumber;
import com.midas.shootpointer.domain.backnumber.entity.BackNumberEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface BackNumberRepository extends JpaRepository<BackNumberEntity,Long> {
    Optional<BackNumberEntity> findByBackNumber(BackNumber backNumber);
}
