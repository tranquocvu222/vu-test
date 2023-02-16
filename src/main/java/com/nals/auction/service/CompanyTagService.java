package com.nals.auction.service;

import com.nals.auction.domain.CompanyTag;
import com.nals.auction.repository.CompanyTagRepository;
import com.nals.utils.service.BaseService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
public class CompanyTagService
    extends BaseService<CompanyTag, CompanyTagRepository> {

    public CompanyTagService(final CompanyTagRepository repository) {
        super(repository);
    }

    public List<CompanyTag> fetchByCompanyId(final Long companyId) {
        log.info("Fetch Company Tag by company #{}", companyId);
        return getRepository().findAllByCompanyId(companyId);
    }

    @Transactional
    public void deleteAllByCompanyId(final Long companyId) {
        log.info("Delete all by companyId #{}", companyId);
        getRepository().deleteAllByCompanyId(companyId);
    }
}
