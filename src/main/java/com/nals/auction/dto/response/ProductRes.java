package com.nals.auction.dto.response;

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
public class ProductRes {

    private Long id;
    private String name;
    private boolean specialCultivationMethod;
    private boolean organicJasMethod;
    private boolean noPesticidesMethod;
    private boolean otherProductionMethod;
}
