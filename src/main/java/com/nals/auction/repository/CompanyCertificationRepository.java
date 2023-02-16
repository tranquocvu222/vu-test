package com.nals.auction.repository;

import com.nals.auction.domain.CompanyCertification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CompanyCertificationRepository
    extends JpaRepository<CompanyCertification, Long> {

    List<CompanyCertification> findAllByCompanyId(Long companyId);

    @Modifying(flushAutomatically = true, clearAutomatically = true)
    @Query("DELETE FROM CompanyCertification cc"
        + " WHERE cc.company.id = :companyId")
    void deleteAllByCompanyId(@Param("companyId") Long companyId);
}
