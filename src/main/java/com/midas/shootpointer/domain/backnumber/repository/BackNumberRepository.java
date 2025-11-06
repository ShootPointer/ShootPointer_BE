package com.midas.shootpointer.domain.backnumber.repository;

import com.midas.shootpointer.domain.backnumber.entity.BackNumber;
import com.midas.shootpointer.domain.backnumber.entity.BackNumberEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface BackNumberRepository extends JpaRepository<BackNumberEntity,Long> {
    Optional<BackNumberEntity> findByBackNumber(BackNumber backNumber);
    Optional<BackNumberEntity> findByBackNumberId(Long backNumberId);

    @Query(value = """
        SELECT b
        FROM BackNumberEntity as b
        INNER JOIN  Member as m ON b. = m.
        """)
    BackNumberEntity findByMemberId(UUID memberId);
}
