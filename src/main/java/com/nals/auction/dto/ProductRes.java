package com.nals.auction.dto;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.nals.auction.dto.response.prefecture.PrefectureRes;
import com.nals.utils.enums.RiceGrainQuality;
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
public class ProductRes {
    private Long id;
    private PrefectureRes prefecture;
    private String name;
    private Long varietyId;
    private String varietyName;
    private boolean dnaIdentification;
    private String productionYear;
    private boolean certificate;
    private String certificateNumber;
    private RiceGrainQuality riceGrainQuality;
    private String sieveMeshSize;
    private boolean specialCultivationMethod;
    private boolean organicJasMethod;
    private boolean noPesticidesMethod;
    private boolean otherProductionMethod;
    private String commitment;
    private String description;
    private String descriptionDetail;
    private String volumetricWeight;
    private String densityMeasuringInstrument;
    private String moisture;
    private String moistureMeasuringInstrument;
    private String whiteImmatureGrain;
    private String deadRice;
    private String splitGrain;
    private String coloredGrain;
    private String crushedGrain;
    private String inspectionDate;
    private boolean heterogeneousGrain;
    private boolean foreignSubstance;
    private String grainDiscriminatorManufacturer;
    private String modelOfEquipmentUsed;
    private String grainThickness;
    private String pesticideResidue;
    private String cadmium;
    private String arsenic;
    private String protein;
    private String amylose;
    private String fattyAcidity;
    private String tasteInformationFirst;
    private String tasteInformationSecond;
    private String tasteInformationThird;
    private ProductCompanyRes company;
    private List<ImageRes> images;
}
