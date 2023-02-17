package com.nals.auction.dto;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class CompanyDto {

    private String name;
    private String imageName;
    private String postalCode;
    private Long townId;
    private String address;
    private String website;
    private String description;
    private String contactName;
    private String contactEmail;
    private String phoneNumber;
    private String faxNumber;
    private List<CertificationDto> certifications;
    private List<CompanyTagDto> companyTags;
}
