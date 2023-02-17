package com.nals.auction.service;

import com.nals.auction.domain.Company;
import com.nals.auction.repository.CompanyRepository;
import com.nals.utils.service.BaseService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Slf4j
@Service
public class CompanyService
    extends BaseService<Company, CompanyRepository> {

    public CompanyService(final CompanyRepository repository) {
        super(repository);
    }

    @Transactional(readOnly = true)
    public Optional<Company> getCompanyById(final Long id) {
        log.info("Get company by id: #{}", id);

        return getRepository().getCompanyById(id);
    }
}
