package com.nals.auction.dto;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class ProductSearchRes {

    private Long id;
    private String name;
    private String varietyName;
    private String productionAreaEn;
    private String productionAreaJa;
    private String productionYear;
    private String certificateNumber;
    private Double moisture;
    private String imageUrl;
}
