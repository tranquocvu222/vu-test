package com.nals.auction.bloc;

import com.nals.auction.client.MasterDataClient;
import com.nals.auction.client.UaaClient;
import com.nals.auction.domain.Company;
import com.nals.auction.domain.CompanyCertification;
import com.nals.auction.domain.CompanyTag;
import com.nals.auction.dto.CertificationDto;
import com.nals.auction.dto.CompanyCertificationDto;
import com.nals.auction.dto.CompanyDto;
import com.nals.auction.dto.CompanyInfoDto;
import com.nals.auction.dto.CompanyTagDto;
import com.nals.auction.dto.LocationDto;
import com.nals.auction.exception.ExceptionHandler;
import com.nals.auction.mapper.MapperHelper;
import com.nals.auction.service.CompanyCertificationService;
import com.nals.auction.service.CompanyService;
import com.nals.auction.service.CompanyTagService;
import com.nals.auction.service.StorageService;
import com.nals.common.messages.errors.ObjectNotFoundException;
import com.nals.common.messages.errors.ValidatorException;
import com.nals.utils.helpers.StringHelper;
import com.nals.utils.helpers.ValidatorHelper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import static com.nals.auction.exception.ExceptionHandler.EXISTS_COMPANY_WITH_USER;
import static com.nals.auction.exception.ExceptionHandler.INVALID_DATA;
import static com.nals.auction.exception.ExceptionHandler.NOT_FOUND;
import static com.nals.auction.exception.ExceptionHandler.OBJECT_NOT_FOUND;
import static com.nals.auction.exception.ExceptionHandler.REQUIRED_NOT_BLANK;
import static com.nals.auction.exception.ExceptionHandler.REQUIRED_NOT_NULL;
import static com.nals.utils.constants.Constants.CERTIFICATION_NAME_MAX_LENGTH;
import static com.nals.utils.constants.Constants.COMPANY_ADDRESS_MAX_LENGTH;
import static com.nals.utils.constants.Constants.COMPANY_CONTACT_NAME_MAX_LENGTH;
import static com.nals.utils.constants.Constants.COMPANY_DESCRIPTION_MAX_LENGTH;
import static com.nals.utils.constants.Constants.COMPANY_NAME_MAX_LENGTH;
import static com.nals.utils.constants.Constants.COMPANY_TAG_NAME_MAX_LENGTH;
import static com.nals.utils.constants.Constants.COMPANY_WEBSITE_MAX_LENGTH;

@Slf4j
@Service
public class CompanyCrudBloc {
    private static final String CERTIFICATION_OTHER = "Other";

    private final CompanyService companyService;
    private final CompanyCertificationService companyCertificationService;
    private final CompanyTagService companyTagService;
    private final ExceptionHandler exceptionHandler;
    private final StorageService storageService;
    private final UaaClient uaaClient;
    private final MasterDataClient masterDataClient;

    public CompanyCrudBloc(final ExceptionHandler exceptionHandler,
                           final UaaClient uaaClient,
                           final MasterDataClient masterDataClient,
                           final StorageService storageService,
                           final CompanyService companyService,
                           final CompanyTagService companyTagService,
                           final CompanyCertificationService companyCertificationService) {
        this.exceptionHandler = exceptionHandler;
        this.uaaClient = uaaClient;
        this.masterDataClient = masterDataClient;
        this.storageService = storageService;
        this.companyService = companyService;
        this.companyTagService = companyTagService;
        this.companyCertificationService = companyCertificationService;
    }

    //TODO create task update dto -> res
    @Transactional
    public Long createCompany(final CompanyDto dto) {
        var currentUser = uaaClient.getCurrentUser();
        var currentUserId = currentUser.getId();
        log.info("Create company for user #{}", currentUserId);

        if (Objects.nonNull(currentUser.getCompanyId())) {
            throw new ValidatorException(exceptionHandler.getMessageCode(EXISTS_COMPANY_WITH_USER),
                                         exceptionHandler.getMessageContent(EXISTS_COMPANY_WITH_USER));
        }

        validateCompanyDto(dto);

        storageService.saveFile(dto.getImageName());
        var company = companyService.save(MapperHelper.INSTANCE.toCompany(dto));
        var companyId = company.getId();

        companyTagService.saveAll(toCompanyTags(dto.getCompanyTags(), companyId));
        companyCertificationService.saveAll(toCompanyCertifications(dto.getCertifications(), companyId));

        uaaClient.attachCompanyToUser(companyId, currentUserId);

        return companyId;
    }

    @Transactional
    public void updateCompany(final CompanyDto dto) {
        var currentUser = uaaClient.getCurrentUser();
        log.info("Update company for user #{}", currentUser.getId());

        var companyId = currentUser.getCompanyId();

        var company = companyService.getById(companyId).orElse(null);
        if (Objects.isNull(companyId) || Objects.isNull(company)) {
            throw new ObjectNotFoundException(exceptionHandler.getMessageCode(OBJECT_NOT_FOUND),
                                              exceptionHandler.getMessageContent(OBJECT_NOT_FOUND));
        }

        validateCompanyDto(dto);

        storageService.replaceFile(dto.getImageName(), company.getImageName());
        companyService.save(companyService.save(MapperHelper.INSTANCE.toCompany(dto, company)));

        companyTagService.deleteAllByCompanyId(companyId);
        companyTagService.saveAll(toCompanyTags(dto.getCompanyTags(), companyId));

        companyCertificationService.deleteAllByCompanyId(companyId);
        companyCertificationService.saveAll(toCompanyCertifications(dto.getCertifications(), companyId));
    }

    @Transactional(readOnly = true)
    public CompanyInfoDto getCompanyDetail() {
        log.info("Get company detail");
        var currentUser = uaaClient.getCurrentUser();
        var companyId = currentUser.getCompanyId();

        var company = companyService.getById(companyId).orElse(null);
        if (Objects.isNull(companyId) || Objects.isNull(company)) {
            throw new ObjectNotFoundException(exceptionHandler.getMessageCode(OBJECT_NOT_FOUND),
                                              exceptionHandler.getMessageContent(OBJECT_NOT_FOUND));
        }

        var companyCertifications = companyCertificationService.fetchByCompanyId(companyId)
                                                               .stream()
                                                               .map(MapperHelper.INSTANCE::toCompanyCertificationDto)
                                                               .collect(Collectors.toList());

        var companyTags = companyTagService.fetchByCompanyId(companyId)
                                           .stream()
                                           .map(MapperHelper.INSTANCE::toCompanyTagDto)
                                           .collect(Collectors.toList());

        var locationDto = masterDataClient.getLocationRes(company.getTownId());
        return convertToCompanyDto(company, companyCertifications, companyTags, locationDto);
    }

    private CompanyInfoDto convertToCompanyDto(final Company company,
                                               final List<CompanyCertificationDto> companyCertificationDtoList,
                                               final List<CompanyTagDto> companyTagDtoList,
                                               final LocationDto locationDto) {
        var companyDto = MapperHelper.INSTANCE.toCompanyDto(company);
        companyDto.setCompanyCertifications(companyCertificationDtoList);
        companyDto.setCompanyTags(companyTagDtoList);
        companyDto.setTown(locationDto.getTown());
        companyDto.setCity(locationDto.getCity());
        companyDto.setPrefecture(locationDto.getPrefecture());
        companyDto.setImageUrl(storageService.getFullFileUrl(companyDto.getImageName()));
        return companyDto;
    }

    private List<CompanyCertification> toCompanyCertifications(final List<CertificationDto> dtoList,
                                                               final Long companyId) {
        if (CollectionUtils.isEmpty(dtoList)) {
            return Collections.emptyList();
        }

        return dtoList.stream().map(certificationDto -> {
            var certification = MapperHelper.INSTANCE.toCompanyCertification(certificationDto);
            certification.setCompany(Company.builder()
                                            .id(companyId)
                                            .build());
            return certification;
        }).collect(Collectors.toList());
    }

    private List<CompanyTag> toCompanyTags(final List<CompanyTagDto> dtoList,
                                           final Long companyId) {
        if (CollectionUtils.isEmpty(dtoList)) {
            return Collections.emptyList();
        }

        return dtoList.stream().map(companyTagDto -> {
            var companyTag = MapperHelper.INSTANCE.toCompanyTag(companyTagDto);
            companyTag.setCompany(Company.builder()
                                         .id(companyId)
                                         .build());
            return companyTag;
        }).collect(Collectors.toList());
    }

    private void validateCompanyDto(final CompanyDto req) {
        var townId = req.getTownId();

        validateCompanyName(req.getName());
        validateTownId(townId);
        validateAddress(req.getAddress());
        validateWebsite(req.getWebsite());
        validateDescription(req.getDescription());
        validateContactName(req.getContactName());
        validateContactEmail(req.getContactEmail());
        validatePhoneNumber(req.getPhoneNumber());
        validateFaxNumber(req.getFaxNumber());

        validateCertificationDtoList(req.getCertifications());
        validateCompanyTagDtoList(req.getCompanyTags());
    }

    private void validateCertificationDtoList(final List<CertificationDto> dtoList) {
        if (CollectionUtils.isEmpty(dtoList)) {
            return;
        }

        var ids = dtoList.stream()
                         .map(CertificationDto::getId)
                         .filter(Objects::nonNull)
                         .collect(Collectors.toList());

        validateCertificateIds(ids, dtoList);

        var certificationOther = masterDataClient.getCertificationsByName(CERTIFICATION_OTHER);

        dtoList.forEach(dto -> {
            var certificationName = dto.getName();

            if (dto.getId().equals(certificationOther.getId())) {
                validateCustomCertification(certificationName);
            } else {
                // If certificate is master data, no need input name
                validateMasterDataCertification(certificationName);
            }
        });
    }

    private void validateCompanyTagDtoList(final List<CompanyTagDto> reqs) {
        if (!CollectionUtils.isEmpty(reqs)) {
            reqs.forEach(r -> validateCompanyTagName(r.getName()));
        }
    }

    private void validateCompanyName(final String companyName) {
        if (StringHelper.isBlank(companyName)) {
            throw new ValidatorException("company_name",
                                         exceptionHandler.getMessageCode(REQUIRED_NOT_BLANK),
                                         exceptionHandler.getMessageContent(REQUIRED_NOT_BLANK));
        }

        if (!ValidatorHelper.isValidMaxLength(companyName, COMPANY_NAME_MAX_LENGTH)) {
            throw new ValidatorException("company_name",
                                         exceptionHandler.getMessageCode(INVALID_DATA),
                                         exceptionHandler.getMessageContent(INVALID_DATA));
        }
    }

    private void validateTownId(final Long townId) {
        if (Objects.isNull(townId)) {
            throw new ValidatorException("town_id",
                                         exceptionHandler.getMessageCode(REQUIRED_NOT_NULL),
                                         exceptionHandler.getMessageContent(REQUIRED_NOT_NULL));
        }

        if (!masterDataClient.existedTownById(townId)) {
            throw new ValidatorException("town_id",
                                         exceptionHandler.getMessageCode(NOT_FOUND),
                                         exceptionHandler.getMessageContent(NOT_FOUND));
        }
    }

    private void validateAddress(final String address) {
        if (StringHelper.isBlank(address)) {
            throw new ValidatorException("address",
                                         exceptionHandler.getMessageCode(REQUIRED_NOT_BLANK),
                                         exceptionHandler.getMessageContent(REQUIRED_NOT_BLANK));
        }

        if (!ValidatorHelper.isValidMaxLength(address, COMPANY_ADDRESS_MAX_LENGTH)) {
            throw new ValidatorException("address",
                                         exceptionHandler.getMessageCode(INVALID_DATA),
                                         exceptionHandler.getMessageContent(INVALID_DATA));
        }
    }

    private void validateWebsite(final String website) {
        if (StringHelper.isNotBlank(website)
            && !ValidatorHelper.isValidMaxLength(website, COMPANY_WEBSITE_MAX_LENGTH)) {
            throw new ValidatorException("website",
                                         exceptionHandler.getMessageCode(INVALID_DATA),
                                         exceptionHandler.getMessageContent(INVALID_DATA));
        }
    }

    private void validateDescription(final String description) {
        if (StringHelper.isNotBlank(description)
            && !ValidatorHelper.isValidMaxLength(description, COMPANY_DESCRIPTION_MAX_LENGTH)) {
            throw new ValidatorException("description",
                                         exceptionHandler.getMessageCode(INVALID_DATA),
                                         exceptionHandler.getMessageContent(INVALID_DATA));
        }
    }

    private void validateContactName(final String contactName) {
        if (StringHelper.isBlank(contactName)) {
            throw new ValidatorException("contact_name",
                                         exceptionHandler.getMessageCode(REQUIRED_NOT_BLANK),
                                         exceptionHandler.getMessageContent(REQUIRED_NOT_BLANK));
        }

        if (!ValidatorHelper.isValidMaxLength(contactName, COMPANY_CONTACT_NAME_MAX_LENGTH)) {
            throw new ValidatorException("contact_name",
                                         exceptionHandler.getMessageCode(INVALID_DATA),
                                         exceptionHandler.getMessageContent(INVALID_DATA));
        }
    }

    private void validateContactEmail(final String contactEmail) {
        if (StringHelper.isBlank(contactEmail)) {
            throw new ValidatorException("contact_email",
                                         exceptionHandler.getMessageCode(REQUIRED_NOT_BLANK),
                                         exceptionHandler.getMessageContent(REQUIRED_NOT_BLANK));
        }

        if (!ValidatorHelper.isValidEmail(contactEmail)) {
            throw new ValidatorException("contact_email",
                                         exceptionHandler.getMessageCode(INVALID_DATA),
                                         exceptionHandler.getMessageContent(INVALID_DATA));
        }
    }

    private void validatePhoneNumber(final String phoneNumber) {
        if (!ValidatorHelper.isValidPhone(phoneNumber)) {
            throw new ValidatorException("phone_number",
                                         exceptionHandler.getMessageCode(INVALID_DATA),
                                         exceptionHandler.getMessageContent(INVALID_DATA));
        }
    }

    private void validateFaxNumber(final String faxNumber) {
        if (StringHelper.isNotBlank(faxNumber) && !ValidatorHelper.isValidPhone(faxNumber)) {
            throw new ValidatorException("fax_number",
                                         exceptionHandler.getMessageCode(INVALID_DATA),
                                         exceptionHandler.getMessageContent(INVALID_DATA));
        }
    }

    private void validateCertificateIds(final List<Long> certificateIds, final List<CertificationDto> reqs) {
        if (certificateIds.size() != reqs.size()) {
            throw new ValidatorException("certificate_id",
                                         exceptionHandler.getMessageCode(INVALID_DATA),
                                         exceptionHandler.getMessageContent(INVALID_DATA));
        }

        if (!(masterDataClient.existedCertificationsByIdIn((certificateIds)))) {
            throw new ValidatorException("certificate_id",
                                         exceptionHandler.getMessageCode(NOT_FOUND),
                                         exceptionHandler.getMessageContent(NOT_FOUND));
        }
    }

    private void validateCustomCertification(final String certificationName) {
        if (!ValidatorHelper.isValidMaxLength(certificationName, CERTIFICATION_NAME_MAX_LENGTH)) {
            throw new ValidatorException("certification_name",
                                         exceptionHandler.getMessageCode(INVALID_DATA),
                                         exceptionHandler.getMessageContent(INVALID_DATA));
        }
    }

    private void validateMasterDataCertification(final String certificationName) {
        if (Objects.nonNull(certificationName)) {
            throw new ValidatorException("certification",
                                         exceptionHandler.getMessageCode(INVALID_DATA),
                                         exceptionHandler.getMessageContent(INVALID_DATA));
        }
    }

    private void validateCompanyTagName(final String companyTagName) {
        if (StringHelper.isBlank(companyTagName)) {
            throw new ValidatorException("company_tag_name",
                                         exceptionHandler.getMessageCode(REQUIRED_NOT_BLANK),
                                         exceptionHandler.getMessageContent(REQUIRED_NOT_BLANK));
        }

        if (!ValidatorHelper.isValidMaxLength(companyTagName, COMPANY_TAG_NAME_MAX_LENGTH)) {
            throw new ValidatorException("company_tag_name",
                                         exceptionHandler.getMessageCode(INVALID_DATA),
                                         exceptionHandler.getMessageContent(INVALID_DATA));
        }
    }
}
