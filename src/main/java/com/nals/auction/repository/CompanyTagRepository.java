package com.nals.auction.repository;

import com.nals.auction.domain.CompanyTag;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CompanyTagRepository
    extends JpaRepository<CompanyTag, Long> {

    List<CompanyTag> findAllByCompanyId(Long companyId);

    @Modifying(flushAutomatically = true, clearAutomatically = true)
    @Query("DELETE FROM CompanyTag ct"
        + " WHERE ct.company.id = :companyId")
    void deleteAllByCompanyId(@Param("companyId") Long companyId);
}
