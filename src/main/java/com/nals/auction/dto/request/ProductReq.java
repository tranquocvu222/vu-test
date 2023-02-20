package com.nals.auction.dto.request;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.nals.utils.enums.RiceGrainQuality;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.util.List;

@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class ProductReq {

    private Long prefectureId;
    private String name;
    private Long varietyId;
    private String varietyName;
    private Boolean dnaIdentification;
    private String productionArea;
    private String productionYear;
    private Boolean certificate;
    private String certificateNumber;
    private RiceGrainQuality riceGrainQuality;
    private String sieveMeshSize;
    private Boolean specialCultivationMethod;
    private Boolean organicJasMethod;
    private Boolean noPesticidesMethod;
    private Boolean otherProductionMethod;
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
    private Boolean heterogeneousGrain;
    private Boolean foreignSubstance;
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
    private List<String> imageNames;
}
