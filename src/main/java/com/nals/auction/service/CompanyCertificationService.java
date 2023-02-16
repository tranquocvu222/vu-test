package com.nals.auction.service;

import com.nals.auction.domain.CompanyCertification;
import com.nals.auction.repository.CompanyCertificationRepository;
import com.nals.utils.service.BaseService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
public class CompanyCertificationService
    extends BaseService<CompanyCertification, CompanyCertificationRepository> {

    public CompanyCertificationService(final CompanyCertificationRepository repository) {
        super(repository);
    }

    @Transactional(readOnly = true)
    public List<CompanyCertification> fetchByCompanyId(final Long companyId) {
        log.info("Fetch Company Certification by company #{}", companyId);
        return getRepository().findAllByCompanyId(companyId);
    }

    @Transactional
    public void deleteAllByCompanyId(final Long companyId) {
        log.info("Delete all by companyId #{}", companyId);
        getRepository().deleteAllByCompanyId(companyId);
    }
}
