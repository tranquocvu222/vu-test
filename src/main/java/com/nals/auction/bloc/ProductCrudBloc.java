package com.nals.auction.bloc;

import com.nals.auction.client.MasterDataClient;
import com.nals.auction.client.UaaClient;
import com.nals.auction.domain.Company;
import com.nals.auction.domain.Media;
import com.nals.auction.dto.request.ProductReq;
import com.nals.auction.dto.ImageRes;
import com.nals.auction.dto.ProductRes;
import com.nals.auction.exception.ExceptionHandler;
import com.nals.auction.mapper.MapperHelper;
import com.nals.auction.service.MediaService;
import com.nals.auction.service.ProductService;
import com.nals.auction.service.StorageService;
import com.nals.common.messages.errors.ObjectNotFoundException;
import com.nals.common.messages.errors.ValidatorException;
import com.nals.utils.enums.MediaType;
import com.nals.utils.helpers.StringHelper;
import com.nals.utils.helpers.ValidatorHelper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.util.StringUtils;

import java.time.Instant;
import java.util.Collection;
import java.util.EnumSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static com.nals.auction.exception.ExceptionHandler.COMPANY_NOT_CREATED;
import static com.nals.auction.exception.ExceptionHandler.INVALID_DATA;
import static com.nals.auction.exception.ExceptionHandler.OBJECT_NOT_FOUND;
import static com.nals.auction.exception.ExceptionHandler.REQUIRED_NOT_BLANK;
import static com.nals.auction.exception.ExceptionHandler.REQUIRED_NOT_NULL;
import static com.nals.utils.constants.Constants.ARSENIC_MAX_LENGTH;
import static com.nals.utils.constants.Constants.CADMIUM_MAX_LENGTH;
import static com.nals.utils.constants.Constants.CERTIFICATE_NUMBER_MAX_LENGTH;
import static com.nals.utils.constants.Constants.COMMITMENT_MAX_LENGTH;
import static com.nals.utils.constants.Constants.DENSITY_MEASURING_MAX_LENGTH;
import static com.nals.utils.constants.Constants.DESCRIPTION_DETAIL_MAX_LENGTH;
import static com.nals.utils.constants.Constants.DESCRIPTION_MAX_LENGTH;
import static com.nals.utils.constants.Constants.GRAIN_DISCRIMINATOR_MANUFACTURER_MAX_LENGTH;
import static com.nals.utils.constants.Constants.GRAIN_THICKNESS_MAX_LENGTH;
import static com.nals.utils.constants.Constants.MODEL_OF_EQUIPMENT_USED_MAX_LENGTH;
import static com.nals.utils.constants.Constants.MOISTURE_MEASURING_INSTRUMENT_MAX_LENGTH;
import static com.nals.utils.constants.Constants.PERCENTAGE_WITH_ONE_FACTION_PATTERN;
import static com.nals.utils.constants.Constants.PESTICIDE_RESIDUE_MAX_LENGTH;
import static com.nals.utils.constants.Constants.PRODUCT_AREA_MAX_LENGTH;
import static com.nals.utils.constants.Constants.PRODUCT_NAME_MAX_LENGTH;
import static com.nals.utils.constants.Constants.SIEVE_MESH_SIZE_PATTERN;
import static com.nals.utils.constants.Constants.TASTE_INFORMATION_FIRST_MAX_LENGTH;
import static com.nals.utils.constants.Constants.TASTE_INFORMATION_SECOND_MAX_LENGTH;
import static com.nals.utils.constants.Constants.TASTE_INFORMATION_THIRD_MAX_LENGTH;
import static com.nals.utils.constants.Constants.VOLUMETRIC_WEIGHT_MAX_LENGTH;
import static com.nals.utils.enums.MediaType.PRODUCT_IMAGE;
import static com.nals.utils.enums.MediaType.PRODUCT_THUMBNAIL;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProductCrudBloc {

    private static final int MAX_IMAGES_UPLOAD = 20;
    public static final String YEAR_PATTERN = "^(19\\d\\d|[2-9]\\d{3})$";
    private static final Set<MediaType> PRODUCT_MEDIA_TYPES = EnumSet.of(PRODUCT_THUMBNAIL, PRODUCT_IMAGE);

    private final ProductService productService;
    private final StorageService storageService;
    private final MediaService mediaService;
    private final ExceptionHandler exceptionHandler;
    private final UaaClient uaaClient;
    private final MasterDataClient masterDataClient;

    @Transactional(readOnly = true)
    public ProductRes getProductById(final Long id) {
        log.info("Get product detail by id: #{}", id);

        var companyId = uaaClient.getCurrentUser().getCompanyId();
        var product = productService
            .getByIdAndCompanyId(id, companyId)
            .orElseThrow(() -> new ObjectNotFoundException("product",
                                                           exceptionHandler.getMessageCode(OBJECT_NOT_FOUND),
                                                           exceptionHandler.getMessageContent(OBJECT_NOT_FOUND)));
        var productRes = MapperHelper.INSTANCE.toProductRes(product);
        productRes.setPrefecture(masterDataClient.getPrefectureById(product.getPrefectureId()));
        List<ImageRes> imageRes = mediaService.fetchBySourceIdAndTypes(id, PRODUCT_MEDIA_TYPES)
                                              .stream()
                                              .map(media -> {
                                                  var mediaName = media.getName();
                                                  return ImageRes.builder()
                                                                 .id(media.getId())
                                                                 .imageUrl(storageService.getFullFileUrl(mediaName))
                                                                 .imageName(mediaName)
                                                                 .type(media.getType())
                                                                 .build();
                                              })
                                              .collect(Collectors.toList());
        productRes.setImages(imageRes);
        return productRes;
    }

    @Transactional
    public Long createProduct(final ProductReq req) {
        var companyId = uaaClient.getCurrentUser().getCompanyId();
        log.info("Create product for companyId #{}", companyId);

        if (Objects.isNull(companyId)) {
            throw new ValidatorException(exceptionHandler.getMessageCode(COMPANY_NOT_CREATED),
                                         exceptionHandler.getMessageContent(COMPANY_NOT_CREATED));
        }

        validateProductReq(req);

        var product = MapperHelper.INSTANCE.toProduct(req);
        product.setCompany(Company.builder()
                                  .id(companyId)
                                  .build());
        productService.save(product);
        var productId = product.getId();
        saveImages(productId, req.getImageNames());

        return productId;
    }

    @Transactional
    public void updateProduct(final Long id, final ProductReq req) {
        var companyId = uaaClient.getCurrentUser().getCompanyId();
        log.info("Update product for companyId #{}", companyId);

        var product = productService
            .getByIdAndCompanyId(id, companyId)
            .orElseThrow(() -> new ObjectNotFoundException(exceptionHandler.getMessageCode(OBJECT_NOT_FOUND),
                                                           exceptionHandler.getMessageContent(OBJECT_NOT_FOUND)));

        validateProductReq(req);

        productService.save(MapperHelper.INSTANCE.toProduct(req, product));
        updateImages(id, req.getImageNames());
    }

    private void saveImages(final Long productId, final List<String> imageNames) {
        if (CollectionUtils.isEmpty(imageNames)) {
            return;
        }

        imageNames.removeIf(String::isBlank);
        List<Media> media = imageNames.stream()
                                      .map(imageName -> Media.builder()
                                                             .sourceId(productId)
                                                             .name(imageName)
                                                             .type(PRODUCT_IMAGE)
                                                             .build())
                                      .collect(Collectors.toList());

        // Set first image as thumbnail
        media.get(0).setType(PRODUCT_THUMBNAIL);

        mediaService.saveAll(media);
        storageService.saveFiles(imageNames);
    }

    private void updateImages(final Long productId, final List<String> imageNames) {
        log.info("Update media with productId #{}", productId);
        List<String> currentImageNames = mediaService.fetchBySourceIdAndTypes(productId, PRODUCT_MEDIA_TYPES)
                                                     .stream()
                                                     .map(Media::getName)
                                                     .collect(Collectors.toList());

        if (!CollectionUtils.isEmpty(currentImageNames) && CollectionUtils.isEmpty(imageNames)) {
            mediaService.deleteBySourceIdAndTypes(productId, PRODUCT_MEDIA_TYPES);
            storageService.deleteFiles(currentImageNames);
            return;
        }

        if (!CollectionUtils.isEmpty(imageNames)) {
            Collection<String> newImageNames = CollectionUtils.subtract(imageNames, currentImageNames);
            Collection<String> removeImageNames = CollectionUtils.subtract(currentImageNames, imageNames);

            // Save images
            List<Media> media = newImageNames.stream()
                                             .map(imageName -> Media.builder()
                                                                    .sourceId(productId)
                                                                    .name(imageName)
                                                                    .type(PRODUCT_IMAGE)
                                                                    .build())
                                             .collect(Collectors.toList());
            mediaService.saveAll(media);
            // Delete unused product images
            mediaService.deleteBySourceIdAndNamesAndTypes(productId, removeImageNames, PRODUCT_MEDIA_TYPES);

            // Save thumbnail image
            if (!mediaService.existsBySourceIdAndType(productId, PRODUCT_THUMBNAIL)) {
                var thumbnailImage = mediaService.findFirstBySourceIdAndType(productId, PRODUCT_IMAGE).orElse(null);

                if (Objects.nonNull(thumbnailImage)) {
                    thumbnailImage.setType(PRODUCT_THUMBNAIL);
                    mediaService.save(thumbnailImage);
                }
            }

            // Save and delete file on S3
            storageService.saveFiles(newImageNames);
            storageService.deleteFiles(removeImageNames);
        }
    }

    private void validateProductReq(final ProductReq req) {
        validateImages(req.getImageNames());
        validateInspectionDate(req.getInspectionDate());

        validateName(req.getName());
        validateVarietyId(req.getVarietyId());
        validateVarietyName(req.getVarietyName());
        validateProductionYear(req.getProductionYear());

        validateProductionArea(req.getProductionArea());
        validateCertificateNumber(req.getCertificateNumber());
        validateCommitment(req.getCommitment());
        validateDescription(req.getDescription());
        validateDescriptionDetail(req.getDescriptionDetail());
        validateVolumetricWeight(req.getVolumetricWeight());
        validateDensityMeasuringInstrument(req.getDensityMeasuringInstrument());
        validateMoistureMeasuringInstrument(req.getMoistureMeasuringInstrument());
        validateGrainDiscriminatorManufacturer(req.getGrainDiscriminatorManufacturer());
        validateModelOfEquipmentUsed(req.getModelOfEquipmentUsed());
        validateGrainThickness(req.getGrainThickness());
        validatePesticideResidue(req.getPesticideResidue());
        validateCadmium(req.getCadmium());
        validateArsenic(req.getArsenic());
        validateTasteInformationFirst(req.getTasteInformationFirst());
        validateTasteInformationSecond(req.getTasteInformationSecond());
        validateTasteInformationThird(req.getTasteInformationThird());

        validateSieveMeshSize(req.getSieveMeshSize());
        validateMoisture(req.getMoisture());
        validateWhiteImmatureGrain(req.getWhiteImmatureGrain());
        validateDeadRice(req.getDeadRice());
        validateSplitGrain(req.getSplitGrain());
        validateColoredGrain(req.getColoredGrain());
        validateCrushedGrain(req.getCrushedGrain());
        validateProtein(req.getProtein());
        validateAmylose(req.getAmylose());
        validateFattyAcidity(req.getFattyAcidity());
    }

    private void validateImages(final List<String> imageNames) {
        if (!CollectionUtils.isEmpty(imageNames) && imageNames.size() > MAX_IMAGES_UPLOAD) {
            throw new ValidatorException("image",
                                         exceptionHandler.getMessageCode(INVALID_DATA),
                                         exceptionHandler.getMessageContent(INVALID_DATA));
        }
    }

    private void validateInspectionDate(final String inspectionDate) {
        if (StringHelper.isBlank(inspectionDate)) {
            return;
        }

        try {
            Instant.parse(inspectionDate);
        } catch (Exception exception) {
            throw new ValidatorException("inspection_date",
                                         exceptionHandler.getMessageCode(INVALID_DATA),
                                         exceptionHandler.getMessageContent(INVALID_DATA));
        }
    }

    private void validateVarietyId(final Long varietyId) {
        if (Objects.isNull(varietyId)) {
            throw new ValidatorException("variety_id",
                                         exceptionHandler.getMessageCode(REQUIRED_NOT_NULL),
                                         exceptionHandler.getMessageContent(REQUIRED_NOT_NULL));
        }
    }

    private void validateName(final String name) {
        if (StringHelper.isBlank(name)) {
            throw new ValidatorException("name",
                                         exceptionHandler.getMessageCode(REQUIRED_NOT_BLANK),
                                         exceptionHandler.getMessageContent(REQUIRED_NOT_BLANK));
        }

        if (!ValidatorHelper.isValidMaxLength(name, PRODUCT_NAME_MAX_LENGTH)) {
            throw new ValidatorException("name",
                                         exceptionHandler.getMessageCode(INVALID_DATA),
                                         exceptionHandler.getMessageContent(INVALID_DATA));
        }
    }

    private void validateVarietyName(final String varietyName) {
        if (StringHelper.isBlank(varietyName)) {
            throw new ValidatorException("variety_name",
                                         exceptionHandler.getMessageCode(REQUIRED_NOT_BLANK),
                                         exceptionHandler.getMessageContent(REQUIRED_NOT_BLANK));
        }

        if (!ValidatorHelper.isValidMaxLength(varietyName, PRODUCT_NAME_MAX_LENGTH)) {
            throw new ValidatorException("variety_name",
                                         exceptionHandler.getMessageCode(INVALID_DATA),
                                         exceptionHandler.getMessageContent(INVALID_DATA));
        }
    }

    private void validateProductionYear(final String productionYear) {
        if (StringHelper.isBlank(productionYear)) {
            throw new ValidatorException("production_year",
                                         exceptionHandler.getMessageCode(REQUIRED_NOT_BLANK),
                                         exceptionHandler.getMessageContent(REQUIRED_NOT_BLANK));
        }

        if (!isValidProductionYear(productionYear)) {
            throw new ValidatorException("production_year",
                                         exceptionHandler.getMessageCode(INVALID_DATA),
                                         exceptionHandler.getMessageContent(INVALID_DATA));
        }
    }

    //TODO move this to ValidatorHelper in common-utils
    private boolean isValidProductionYear(final String year) {
        if (!StringUtils.hasText(year)) {
            return false;
        }

        return Pattern.compile(YEAR_PATTERN)
                      .matcher(year)
                      .matches();
    }

    private void validateProductionArea(final String productionArea) {
        if (StringHelper.isNotBlank(productionArea)
            && !ValidatorHelper.isValidMaxLength(productionArea, PRODUCT_AREA_MAX_LENGTH)) {
            throw new ValidatorException("production_area",
                                         exceptionHandler.getMessageCode(INVALID_DATA),
                                         exceptionHandler.getMessageContent(INVALID_DATA));
        }
    }

    private void validateCertificateNumber(final String certificateNumber) {
        if (StringHelper.isNotBlank(certificateNumber)
            && !ValidatorHelper.isValidMaxLength(certificateNumber, CERTIFICATE_NUMBER_MAX_LENGTH)) {
            throw new ValidatorException("certificate_number",
                                         exceptionHandler.getMessageCode(INVALID_DATA),
                                         exceptionHandler.getMessageContent(INVALID_DATA));
        }
    }

    private void validateCommitment(final String commitment) {
        if (StringHelper.isNotBlank(commitment)
            && !ValidatorHelper.isValidMaxLength(commitment, COMMITMENT_MAX_LENGTH)) {
            throw new ValidatorException("commitment",
                                         exceptionHandler.getMessageCode(INVALID_DATA),
                                         exceptionHandler.getMessageContent(INVALID_DATA));
        }
    }

    private void validateDescription(final String description) {
        if (StringHelper.isNotBlank(description)
            && !ValidatorHelper.isValidMaxLength(description, DESCRIPTION_MAX_LENGTH)) {
            throw new ValidatorException("description",
                                         exceptionHandler.getMessageCode(INVALID_DATA),
                                         exceptionHandler.getMessageContent(INVALID_DATA));
        }
    }

    private void validateDescriptionDetail(final String descriptionDetail) {
        if (StringHelper.isNotBlank(descriptionDetail)
            && !ValidatorHelper.isValidMaxLength(descriptionDetail, DESCRIPTION_DETAIL_MAX_LENGTH)) {
            throw new ValidatorException("description_detail",
                                         exceptionHandler.getMessageCode(INVALID_DATA),
                                         exceptionHandler.getMessageContent(INVALID_DATA));
        }
    }

    private void validateVolumetricWeight(final String volumetricWeight) {
        if (StringHelper.isNotBlank(volumetricWeight)
            && !ValidatorHelper.isValidMaxLength(volumetricWeight, VOLUMETRIC_WEIGHT_MAX_LENGTH)) {
            throw new ValidatorException("volumetric_weight",
                                         exceptionHandler.getMessageCode(INVALID_DATA),
                                         exceptionHandler.getMessageContent(INVALID_DATA));
        }
    }

    private void validateDensityMeasuringInstrument(final String densityMeasuringInstrument) {
        if (StringHelper.isNotBlank(densityMeasuringInstrument)
            && !ValidatorHelper.isValidMaxLength(densityMeasuringInstrument, DENSITY_MEASURING_MAX_LENGTH)) {
            throw new ValidatorException("density_measuring_instrument",
                                         exceptionHandler.getMessageCode(INVALID_DATA),
                                         exceptionHandler.getMessageContent(INVALID_DATA));
        }
    }

    private void validateMoistureMeasuringInstrument(final String moistureMeasuringInstrument) {
        if (StringHelper.isNotBlank(moistureMeasuringInstrument)
            && !ValidatorHelper.isValidMaxLength(moistureMeasuringInstrument,
                                                 MOISTURE_MEASURING_INSTRUMENT_MAX_LENGTH)) {
            throw new ValidatorException("moisture_measuring_instrument",
                                         exceptionHandler.getMessageCode(INVALID_DATA),
                                         exceptionHandler.getMessageContent(INVALID_DATA));
        }
    }

    private void validateGrainDiscriminatorManufacturer(final String grainDiscriminatorManufacturer) {
        if (StringHelper.isNotBlank(grainDiscriminatorManufacturer)
            && !ValidatorHelper.isValidMaxLength(grainDiscriminatorManufacturer,
                                                 GRAIN_DISCRIMINATOR_MANUFACTURER_MAX_LENGTH)) {
            throw new ValidatorException("grain_discriminator_manufacturer",
                                         exceptionHandler.getMessageCode(INVALID_DATA),
                                         exceptionHandler.getMessageContent(INVALID_DATA));
        }
    }

    private void validateModelOfEquipmentUsed(final String modelOfEquipmentUsed) {
        if (StringHelper.isNotBlank(modelOfEquipmentUsed)
            && !ValidatorHelper.isValidMaxLength(modelOfEquipmentUsed, MODEL_OF_EQUIPMENT_USED_MAX_LENGTH)) {
            throw new ValidatorException("model_of_equipment_used",
                                         exceptionHandler.getMessageCode(INVALID_DATA),
                                         exceptionHandler.getMessageContent(INVALID_DATA));
        }
    }

    private void validateGrainThickness(final String grainThickness) {
        if (StringHelper.isNotBlank(grainThickness)
            && !ValidatorHelper.isValidMaxLength(grainThickness, GRAIN_THICKNESS_MAX_LENGTH)) {
            throw new ValidatorException("grain_thickness",
                                         exceptionHandler.getMessageCode(INVALID_DATA),
                                         exceptionHandler.getMessageContent(INVALID_DATA));
        }
    }

    private void validatePesticideResidue(final String pesticideResidue) {
        if (StringHelper.isNotBlank(pesticideResidue)
            && !ValidatorHelper.isValidMaxLength(pesticideResidue, PESTICIDE_RESIDUE_MAX_LENGTH)) {
            throw new ValidatorException("pesticide_residue",
                                         exceptionHandler.getMessageCode(INVALID_DATA),
                                         exceptionHandler.getMessageContent(INVALID_DATA));
        }
    }

    private void validateCadmium(final String cadmium) {
        if (StringHelper.isNotBlank(cadmium)
            && !ValidatorHelper.isValidMaxLength(cadmium, CADMIUM_MAX_LENGTH)) {
            throw new ValidatorException("cadmium",
                                         exceptionHandler.getMessageCode(INVALID_DATA),
                                         exceptionHandler.getMessageContent(INVALID_DATA));
        }
    }

    private void validateArsenic(final String arsenic) {
        if (StringHelper.isNotBlank(arsenic)
            && !ValidatorHelper.isValidMaxLength(arsenic, ARSENIC_MAX_LENGTH)) {
            throw new ValidatorException("arsenic",
                                         exceptionHandler.getMessageCode(INVALID_DATA),
                                         exceptionHandler.getMessageContent(INVALID_DATA));
        }
    }

    private void validateTasteInformationFirst(final String tasteInformationFirst) {
        if (StringHelper.isNotBlank(tasteInformationFirst)
            && !ValidatorHelper.isValidMaxLength(tasteInformationFirst, TASTE_INFORMATION_FIRST_MAX_LENGTH)) {
            throw new ValidatorException("taste_information_first",
                                         exceptionHandler.getMessageCode(INVALID_DATA),
                                         exceptionHandler.getMessageContent(INVALID_DATA));
        }
    }

    private void validateTasteInformationSecond(final String tasteInformationSecond) {
        if (StringHelper.isNotBlank(tasteInformationSecond)
            && !ValidatorHelper.isValidMaxLength(tasteInformationSecond, TASTE_INFORMATION_SECOND_MAX_LENGTH)) {
            throw new ValidatorException("taste_information_second",
                                         exceptionHandler.getMessageCode(INVALID_DATA),
                                         exceptionHandler.getMessageContent(INVALID_DATA));
        }
    }

    private void validateTasteInformationThird(final String tasteInformationThird) {
        if (StringHelper.isNotBlank(tasteInformationThird)
            && !ValidatorHelper.isValidMaxLength(tasteInformationThird, TASTE_INFORMATION_THIRD_MAX_LENGTH)) {
            throw new ValidatorException("taste_information_third",
                                         exceptionHandler.getMessageCode(INVALID_DATA),
                                         exceptionHandler.getMessageContent(INVALID_DATA));
        }
    }

    private void validateSieveMeshSize(final String sieveMeshSize) {
        if (StringHelper.isNotBlank(sieveMeshSize)
            && !ValidatorHelper.isValidNumberWithPattern(sieveMeshSize, SIEVE_MESH_SIZE_PATTERN)) {
            throw new ValidatorException("sieve_mesh_size",
                                         exceptionHandler.getMessageCode(INVALID_DATA),
                                         exceptionHandler.getMessageContent(INVALID_DATA));
        }
    }

    private void validateMoisture(final String moisture) {
        if (StringHelper.isBlank(moisture)) {
            throw new ValidatorException("moisture",
                                         exceptionHandler.getMessageCode(REQUIRED_NOT_BLANK),
                                         exceptionHandler.getMessageCode(REQUIRED_NOT_BLANK));
        }

        if (StringHelper.isNotBlank(moisture)
            && !ValidatorHelper.isValidNumberWithPattern(moisture, PERCENTAGE_WITH_ONE_FACTION_PATTERN)) {
            throw new ValidatorException("moisture",
                                         exceptionHandler.getMessageCode(INVALID_DATA),
                                         exceptionHandler.getMessageContent(INVALID_DATA));
        }
    }

    private void validateWhiteImmatureGrain(final String whiteImmatureGrain) {
        if (StringHelper.isNotBlank(whiteImmatureGrain)
            && !ValidatorHelper.isValidNumberWithPattern(whiteImmatureGrain, PERCENTAGE_WITH_ONE_FACTION_PATTERN)) {
            throw new ValidatorException("white_immature_grain",
                                         exceptionHandler.getMessageCode(INVALID_DATA),
                                         exceptionHandler.getMessageContent(INVALID_DATA));
        }
    }

    private void validateDeadRice(final String deadRice) {
        if (StringHelper.isNotBlank(deadRice)
            && !ValidatorHelper.isValidNumberWithPattern(deadRice, PERCENTAGE_WITH_ONE_FACTION_PATTERN)) {
            throw new ValidatorException("dead_rice",
                                         exceptionHandler.getMessageCode(INVALID_DATA),
                                         exceptionHandler.getMessageContent(INVALID_DATA));
        }
    }

    private void validateSplitGrain(final String splitGrain) {
        if (StringHelper.isNotBlank(splitGrain)
            && !ValidatorHelper.isValidNumberWithPattern(splitGrain, PERCENTAGE_WITH_ONE_FACTION_PATTERN)) {
            throw new ValidatorException("split_grain",
                                         exceptionHandler.getMessageCode(INVALID_DATA),
                                         exceptionHandler.getMessageContent(INVALID_DATA));
        }
    }

    private void validateColoredGrain(final String coloredGrain) {
        if (StringHelper.isNotBlank(coloredGrain)
            && !ValidatorHelper.isValidNumberWithPattern(coloredGrain, PERCENTAGE_WITH_ONE_FACTION_PATTERN)) {
            throw new ValidatorException("colored_grain",
                                         exceptionHandler.getMessageCode(INVALID_DATA),
                                         exceptionHandler.getMessageContent(INVALID_DATA));
        }
    }

    private void validateCrushedGrain(final String crushedGrain) {
        if (StringHelper.isNotBlank(crushedGrain)
            && !ValidatorHelper.isValidNumberWithPattern(crushedGrain, PERCENTAGE_WITH_ONE_FACTION_PATTERN)) {
            throw new ValidatorException("crushed_grain",
                                         exceptionHandler.getMessageCode(INVALID_DATA),
                                         exceptionHandler.getMessageContent(INVALID_DATA));
        }
    }

    private void validateProtein(final String protein) {
        if (StringHelper.isNotBlank(protein)
            && !ValidatorHelper.isValidNumberWithPattern(protein, PERCENTAGE_WITH_ONE_FACTION_PATTERN)) {
            throw new ValidatorException("protein",
                                         exceptionHandler.getMessageCode(INVALID_DATA),
                                         exceptionHandler.getMessageContent(INVALID_DATA));
        }
    }

    private void validateAmylose(final String amylose) {
        if (StringHelper.isNotBlank(amylose)
            && !ValidatorHelper.isValidNumberWithPattern(amylose, PERCENTAGE_WITH_ONE_FACTION_PATTERN)) {
            throw new ValidatorException("amylose",
                                         exceptionHandler.getMessageCode(INVALID_DATA),
                                         exceptionHandler.getMessageContent(INVALID_DATA));
        }
    }

    private void validateFattyAcidity(final String fattyAcidity) {
        if (StringHelper.isNotBlank(fattyAcidity)
            && !ValidatorHelper.isValidNumberWithPattern(fattyAcidity, PERCENTAGE_WITH_ONE_FACTION_PATTERN)) {
            throw new ValidatorException("fatty_acidity",
                                         exceptionHandler.getMessageCode(INVALID_DATA),
                                         exceptionHandler.getMessageContent(INVALID_DATA));
        }
    }
}
