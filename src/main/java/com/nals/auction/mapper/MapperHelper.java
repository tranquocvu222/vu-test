package com.nals.auction.mapper;

import com.nals.auction.domain.Company;
import com.nals.auction.domain.CompanyCertification;
import com.nals.auction.domain.CompanyTag;
import com.nals.auction.dto.CertificationDto;
import com.nals.auction.dto.CompanyCertificationDto;
import com.nals.auction.dto.CompanyDto;
import com.nals.auction.dto.CompanyInfoDto;
import com.nals.auction.dto.CompanyTagDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Mappings;
import org.mapstruct.NullValueCheckStrategy;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE,
    nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS,
    nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface MapperHelper {
    MapperHelper INSTANCE = Mappers.getMapper(MapperHelper.class);


    Company toCompany(CompanyDto companyDto);

    @Mappings({
        @Mapping(target = "id", ignore = true),
    })
    Company toCompany(CompanyDto companyDto, @MappingTarget Company company);

    CompanyInfoDto toCompanyDto(Company company);

    CompanyTag toCompanyTag(CompanyTagDto companyTagDto);

    @Mapping(target = "certificationId", source = "id")
    CompanyCertification toCompanyCertification(CertificationDto certificationDto);

    CompanyCertificationDto toCompanyCertificationDto(CompanyCertification certification);

    CompanyTagDto toCompanyTagDto(CompanyTag companyTag);
}
