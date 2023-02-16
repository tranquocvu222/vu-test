package com.nals.auction.repository;

import com.nals.auction.domain.Company;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CompanyRepository
    extends JpaRepository<Company, Long> {

    Optional<Company> findOneById(Long id);

    //TODO update add join companyTag and companyCetification
    @Query("SELECT new Company(c.id, c.name, c.imageName, c.address, c.website, c.description, c.contactName,"
        + "                    c.contactEmail, c.phoneNumber, c.faxNumber)"
        + " FROM Company c"
        + " WHERE c.id = :id")
    Optional<Company> getCompanyById(@Param("id") Long id);
}
