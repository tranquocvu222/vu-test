package com.nals.auction.domain;

import com.nals.utils.enums.RiceGrainQuality;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import java.time.Instant;

import static javax.persistence.EnumType.STRING;

@Entity
@Getter
@Setter
@ToString
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "products")
public class Product
    extends AbstractAuditingEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "prefecture_id", nullable = false)
    private Long prefectureId;

    @Column(nullable = false)
    private String name;

    @Column(name = "variety_name", nullable = false)
    private String varietyName;

    @Builder.Default
    @Column(name = "dna_identification")
    private boolean dnaIdentification = false;

    @Column(name = "production_area", nullable = false)
    private String productionArea;

    @Column(name = "production_year", length = 4, nullable = false)
    private String productionYear;

    @Column
    @Builder.Default
    private boolean certificate = false;

    @Column(name = "certificate_number")
    private String certificateNumber;

    @Enumerated(STRING)
    @Column(name = "rice_grain_quality", length = 20)
    private RiceGrainQuality riceGrainQuality;

    @Column(name = "sieve_mesh_size")
    private Double sieveMeshSize;

    @Builder.Default
    @Column(name = "special_cultivation_method")
    private boolean specialCultivationMethod = false;

    @Builder.Default
    @Column(name = "organic_jas_method")
    private boolean organicJasMethod = false;

    @Builder.Default
    @Column(name = "no_pesticides_method")
    private boolean noPesticidesMethod = false;

    @Builder.Default
    @Column(name = "other_production_method")
    private boolean otherProductionMethod = false;

    @Column(length = 500)
    private String commitment;

    @Lob
    @Column(length = 1000, columnDefinition = "text")
    private String description;

    @Lob
    @Column(name = "description_detail", length = 1000, columnDefinition = "text")
    private String descriptionDetail;

    @Column(name = "volumetric_weight")
    private String volumetricWeight;

    @Column(name = "density_measuring_instrument")
    private String densityMeasuringInstrument;

    @Column(nullable = false)
    private Double moisture;

    @Column(name = "moisture_measuring_instrument")
    private String moistureMeasuringInstrument;

    @Column(name = "white_immature_grain")
    private Double whiteImmatureGrain;

    @Column(name = "dead_rice")
    private Double deadRice;

    @Column(name = "split_grain")
    private Double splitGrain;

    @Column(name = "colored_grain")
    private Double coloredGrain;

    @Column(name = "crushed_grain")
    private Double crushedGrain;

    @Column(name = "inspection_date")
    private Instant inspectionDate;

    @Builder.Default
    @Column(name = "heterogeneous_grain")
    private boolean heterogeneousGrain = false;

    @Builder.Default
    @Column(name = "foreign_substance")
    private boolean foreignSubstance = false;

    @Column(name = "grain_discriminator_manufacturer")
    private String grainDiscriminatorManufacturer;

    @Column(name = "model_of_equipment_used")
    private String modelOfEquipmentUsed;

    @Column(name = "grain_thickness")
    private String grainThickness;

    @Column(name = "pesticide_residue")
    private String pesticideResidue;

    @Column
    private String cadmium;

    @Column
    private String arsenic;

    @Column
    private Double protein;

    @Column
    private Double amylose;

    @Column(name = "fatty_acidity")
    private Double fattyAcidity;

    @Column(name = "taste_information_first")
    private String tasteInformationFirst;

    @Column(name = "taste_information_second")
    private String tasteInformationSecond;

    @Column(name = "taste_information_third")
    private String tasteInformationThird;

    @ToString.Exclude
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "company_id")
    private Company company;
}
