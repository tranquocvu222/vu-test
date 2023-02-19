package com.nals.auction.mapper;

import com.nals.auction.domain.Auction;
import com.nals.auction.domain.Company;
import com.nals.auction.domain.CompanyCertification;
import com.nals.auction.domain.CompanyTag;
import com.nals.auction.domain.Product;
import com.nals.auction.dto.CertificationDto;
import com.nals.auction.dto.CompanyCertificationDto;
import com.nals.auction.dto.CompanyDto;
import com.nals.auction.dto.CompanyInfoDto;
import com.nals.auction.dto.CompanyTagDto;
import com.nals.auction.dto.ProductSearchRes;
import com.nals.auction.dto.request.product.ProductCreateReq;
import com.nals.auction.dto.response.AuctionRes;
import com.nals.auction.dto.response.auction.AuctionDetailRes;
import com.nals.utils.helpers.DateHelper;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Mappings;
import org.mapstruct.NullValueCheckStrategy;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;

import java.time.Instant;

import static com.nals.utils.constants.Constants.UTC_ZONE_NAME;

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

    @Mapping(target = "certificateNumber", source = "certificate")
    AuctionRes toAuctionRes(Auction auction);

    @Mappings({
        @Mapping(target = "productCertificate", source = "certificate"),
        @Mapping(target = "productionArea", ignore = true)
    })
    AuctionDetailRes toAuctionDetailRes(Auction auction);

    @Mapping(target = "certificationId", source = "id")
    CompanyCertification toCompanyCertification(CertificationDto certificationDto);

    CompanyCertificationDto toCompanyCertificationDto(CompanyCertification certification);

    CompanyTagDto toCompanyTagDto(CompanyTag companyTag);

    Product toProduct(ProductCreateReq req);

    ProductSearchRes toProductSearchRes(Product product);

    default Instant toInstant(String dateTime) {
        return DateHelper.toInstant(dateTime);
    }

    default String fromInstant(Instant instant) {
        return DateHelper.toStringISOFormatWithZoneName(instant, UTC_ZONE_NAME);
    }
}
