package com.nals.auction.bloc;

import com.nals.auction.client.MasterDataClient;
import com.nals.auction.client.UaaClient;
import com.nals.auction.domain.Auction;
import com.nals.auction.dto.UserDto;
import com.nals.auction.dto.request.AuctionReq;
import com.nals.auction.dto.request.DeliveryDateReq;
import com.nals.auction.dto.request.PaymentMethodReq;
import com.nals.auction.dto.request.TradingDeadlineReq;
import com.nals.auction.dto.response.auction.AuctionDetailRes;
import com.nals.auction.dto.response.prefecture.PrefectureRes;
import com.nals.auction.exception.ExceptionHandler;
import com.nals.auction.mapper.MapperHelper;
import com.nals.auction.service.AuctionService;
import com.nals.auction.service.CompanyService;
import com.nals.auction.service.MediaService;
import com.nals.auction.service.ProductService;
import com.nals.auction.service.StorageService;
import com.nals.common.messages.errors.ObjectNotFoundException;
import com.nals.common.messages.errors.ValidatorException;
import com.nals.utils.enums.AuctionStatus;
import com.nals.utils.enums.DeliveryDateType;
import com.nals.utils.enums.MediaType;
import com.nals.utils.enums.PaymentMethod;
import com.nals.utils.enums.TradingDeadlineType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.format.DateTimeParseException;
import java.util.Arrays;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;

import static com.nals.auction.exception.ExceptionHandler.INVALID_DATA;
import static com.nals.auction.exception.ExceptionHandler.OBJECT_NOT_FOUND;
import static com.nals.auction.exception.ExceptionHandler.PUBLISH_AUCTION_FAILED;
import static com.nals.auction.exception.ExceptionHandler.REQUIRED_NOT_NULL;
import static com.nals.utils.enums.AuctionStatus.DRAFT;
import static com.nals.utils.enums.AuctionStatus.OPEN;
import static com.nals.utils.enums.DeliveryDateType.WITHIN_A_MONTH;
import static java.time.temporal.ChronoUnit.HOURS;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuctionCrudBloc {

    private final AuctionService auctionService;
    private final ProductService productService;
    private final CompanyService companyService;
    private final MediaService mediaService;
    private final StorageService storageService;
    private final UaaClient uaaClient;
    private final MasterDataClient masterDataClient;
    private final ExceptionHandler exceptionHandler;

    @Transactional(readOnly = true)
    public AuctionDetailRes getAuctionById(final Long id) {
        log.info("Get auction by id #{}", id);
        var auction =
            auctionService
                .getById(id)
                .orElseThrow(() -> new ObjectNotFoundException("auction",
                                                               exceptionHandler.getMessageCode(OBJECT_NOT_FOUND),
                                                               exceptionHandler.getMessageContent(OBJECT_NOT_FOUND)));

        var media = mediaService.getBySourceIdAndType(auction.getProduct().getId(), MediaType.PRODUCT_THUMBNAIL)
                                .orElse(null);

        var auctionPrefectureId = auction.getPrefectureId();
        var productPrefectureId = auction.getProduct().getPrefectureId();
        var prefectureIds = Arrays.asList(auctionPrefectureId, productPrefectureId);
        var prefectureResMap = masterDataClient.fetchPrefectureRes(prefectureIds)
                                               .stream()
                                               .collect(Collectors.toMap(PrefectureRes::getId, Function.identity()));

        var auctionRes = MapperHelper.INSTANCE.toAuctionDetailRes(auction);

        auctionRes.setProductionArea(prefectureResMap.get(productPrefectureId));
        auctionRes.setPrefectureRes(prefectureResMap.get(auctionPrefectureId));

        if (Objects.nonNull(media)) {
            auctionRes.setImageName(media.getName());
            auctionRes.setProductImage(storageService.getFullFileUrl(media.getName()));
        }

        //TODO mapping other and delivery if type is WITHIN_SOME_DAYS and type is OTHER
        auctionRes.setDeliveryDateType(getDeliveryDate(auction));
        auctionRes.setTradingDeadlineType(getTradingDeadline(auction));
        auctionRes.setPaymentMethod(getPaymentMethod(auction));

        return auctionRes;
    }

    @Transactional
    public Long createAuction(final AuctionReq req) {
        var currentUser = uaaClient.getCurrentUser();
        var currentTime = Instant.now();
        log.info("Create auction with data #{} by user #{}", req, currentUser.getId());

        validateCreateAuction(req, currentTime);

        var auction = auctionService.save(buildAuction(req, currentUser, currentTime));

        return auction.getId();
    }

    @Transactional
    public void publishAuction(final Long id) {
        log.info("Publish auction by id #{}", id);

        var companyId = uaaClient.getCurrentUser().getCompanyId();
        var auction =
            auctionService
                .getAuctionByIdAndCompanyIdAndStatus(id, companyId, DRAFT)
                .orElseThrow(() -> new ObjectNotFoundException("auction",
                                                               exceptionHandler.getMessageCode(OBJECT_NOT_FOUND),
                                                               exceptionHandler.getMessageContent(OBJECT_NOT_FOUND)));
        var now = Instant.now();

        if (now.isBefore(auction.getStartTime()) || (now.plus(24, HOURS)).isAfter(auction.getEndTime())) {
            throw new ValidatorException(exceptionHandler.getMessageCode(PUBLISH_AUCTION_FAILED),
                                         exceptionHandler.getMessageContent(PUBLISH_AUCTION_FAILED));
        }

        auction.setStatus(OPEN);
        auction.setStartTime(now);
        auctionService.save(auction);
    }

    @Transactional
    public void unpublishAuction(final Long id) {
        log.info("Unpublish auction by id #{}", id);

        var companyId = uaaClient.getCurrentUser().getCompanyId();
        var auction =
            auctionService
                .getAuctionByIdAndCompanyIdAndStatus(id, companyId, OPEN)
                .orElseThrow(() -> new ObjectNotFoundException("auction",
                                                               exceptionHandler.getMessageCode(OBJECT_NOT_FOUND),
                                                               exceptionHandler.getMessageContent(OBJECT_NOT_FOUND)));

        //TODO verify condition

        auction.setStatus(DRAFT);
        auctionService.save(auction);
    }

    private Auction buildAuction(final AuctionReq req, final UserDto userDto, final Instant currentTime) {
        var product =
            productService
                .getByIdAndCompanyId(req.getProductId(), userDto.getCompanyId())
                .orElseThrow(() -> new ObjectNotFoundException("product",
                                                               exceptionHandler.getMessageCode(OBJECT_NOT_FOUND),
                                                               exceptionHandler.getMessageContent(OBJECT_NOT_FOUND)));
        var company =
            companyService
                .getById(userDto.getCompanyId())
                .orElseThrow(() -> new ObjectNotFoundException("company",
                                                               exceptionHandler.getMessageCode(OBJECT_NOT_FOUND),
                                                               exceptionHandler.getMessageContent(OBJECT_NOT_FOUND)));
        //TODO validate new requirement
        var deliveryDateType = req.getDeliveryDate().getType();
        var tradingDeadlineType = req.getTradingDeadline().getType();
        var paymentMethodType = req.getPaymentMethod().getType();

        var auction = Auction.builder()
                             .userId(userDto.getId())
                             .productName(product.getName())
                             .varietyName(product.getVarietyName())
                             .productionArea(product.getProductionArea())
                             .productionYear(product.getProductionYear())
                             .moisture(product.getMoisture())
                             .certificate(product.getCertificateNumber())
                             .startTime(currentTime)
                             .endTime(validateAndGetEndTime(req.getEndTime(), currentTime))
                             .quantity(req.getQuantity())
                             .startPrice(req.getStartPrice())
                             .prefectureId(req.getPrefectureId())
                             .status(AuctionStatus.DRAFT)
                             .deliveryDateType(deliveryDateType)
                             .tradingDeadlineType(tradingDeadlineType)
                             .paymentMethod(paymentMethodType)
                             .logisticsArrangementType(req.getLogisticsArrangementType())
                             .product(product)
                             .company(company)
                             .build();

        //TODO validate value
        if (DeliveryDateType.OTHER == deliveryDateType || DeliveryDateType.WITHIN_SOME_DAYS == deliveryDateType) {
            auction.setDeliveryDateValue(req.getDeliveryDate().getValue());
        }

        if (TradingDeadlineType.OTHER == tradingDeadlineType) {
            auction.setTradingDeadlineValue(req.getTradingDeadline().getValue());
        }

        if (PaymentMethod.OTHER == paymentMethodType) {
            auction.setPaymentMethodValue(req.getPaymentMethod().getValue());
        }

        return auction;
    }

    private void validateCreateAuction(final AuctionReq req, final Instant currentTime) {
        validateProductId(req.getProductId());
        validateAndGetEndTime(req.getEndTime(), currentTime);
        validateQuantity(req.getQuantity());
        validateStartPrice(req.getStartPrice());
        validatePrefectureId(req.getPrefectureId());
        validateDeliveryDateType(req.getDeliveryDate());
        validateTradingDeadlineType(req.getTradingDeadline());
        validatePaymentMethod(req.getPaymentMethod());
    }

    private void validateProductId(final Long productId) {
        if (Objects.isNull(productId)) {
            throw new ValidatorException("product_id",
                                         exceptionHandler.getMessageCode(REQUIRED_NOT_NULL),
                                         exceptionHandler.getMessageContent(REQUIRED_NOT_NULL));
        }
    }

    private Instant validateAndGetEndTime(final String endTime, final Instant currentTime) {
        if (Objects.isNull(endTime)) {
            throw new ValidatorException("end_time",
                                         exceptionHandler.getMessageCode(REQUIRED_NOT_NULL),
                                         exceptionHandler.getMessageContent(REQUIRED_NOT_NULL));
        }

        Instant time;

        try {
            time = Instant.parse(endTime);
        } catch (DateTimeParseException exception) {
            throw new ValidatorException("end_time",
                                         exceptionHandler.getMessageCode(INVALID_DATA),
                                         exceptionHandler.getMessageContent(INVALID_DATA));
        }

        if (time.isBefore(currentTime.plus(24, HOURS))) {
            throw new ValidatorException("end_time",
                                         exceptionHandler.getMessageCode(INVALID_DATA),
                                         exceptionHandler.getMessageContent(INVALID_DATA));
        }

        return time;
    }

    private void validateQuantity(final Long quantity) {
        if (Objects.isNull(quantity)) {
            throw new ValidatorException("quantity",
                                         exceptionHandler.getMessageCode(REQUIRED_NOT_NULL),
                                         exceptionHandler.getMessageContent(REQUIRED_NOT_NULL));
        }
    }

    private void validateStartPrice(final BigDecimal startPrice) {
        if (Objects.isNull(startPrice)) {
            throw new ValidatorException("start_price",
                                         exceptionHandler.getMessageCode(REQUIRED_NOT_NULL),
                                         exceptionHandler.getMessageContent(REQUIRED_NOT_NULL));
        }
    }

    private void validatePrefectureId(final Long prefectureId) {
        if (Objects.isNull(prefectureId)) {
            throw new ValidatorException("prefecture_id",
                                         exceptionHandler.getMessageCode(REQUIRED_NOT_NULL),
                                         exceptionHandler.getMessageContent(REQUIRED_NOT_NULL));
        }
    }

    private void validateDeliveryDateType(final DeliveryDateReq deliveryDateReq) {
        if (WITHIN_A_MONTH == deliveryDateReq.getType() || DeliveryDateType.OTHER == deliveryDateReq.getType()) {
            if (Objects.isNull(deliveryDateReq.getValue())) {
                throw new ValidatorException("delivery_date_type",
                                             exceptionHandler.getMessageCode(INVALID_DATA),
                                             exceptionHandler.getMessageContent(INVALID_DATA));
            }
        }
        //TODO validate new required
    }

    private void validateTradingDeadlineType(final TradingDeadlineReq tradingDeadlineReq) {
        if (TradingDeadlineType.OTHER == tradingDeadlineReq.getType()) {
            if (Objects.isNull(tradingDeadlineReq.getValue())) {
                throw new ValidatorException("trading_deadline_type",
                                             exceptionHandler.getMessageCode(INVALID_DATA),
                                             exceptionHandler.getMessageContent(INVALID_DATA));
            }
        }
    }

    private void validatePaymentMethod(final PaymentMethodReq paymentMethodReq) {
        if (PaymentMethod.OTHER == paymentMethodReq.getType()) {
            if (Objects.isNull(paymentMethodReq.getValue())) {
                throw new ValidatorException("payment_method",
                                             exceptionHandler.getMessageCode(INVALID_DATA),
                                             exceptionHandler.getMessageContent(INVALID_DATA));
            }
        }
    }

    //TODO change response
    private String getDeliveryDate(final Auction auction) {
        var deliveryDateType = auction.getDeliveryDateType();
        if (DeliveryDateType.WITHIN_SOME_DAYS == deliveryDateType
            || DeliveryDateType.OTHER == deliveryDateType) {
            return auction.getDeliveryDateValue();
        }

        return deliveryDateType.name();
    }

    //TODO change response
    private String getTradingDeadline(final Auction auction) {
        var tradingDeadlineType = auction.getTradingDeadlineType();
        if (TradingDeadlineType.OTHER == tradingDeadlineType) {
            return auction.getTradingDeadlineValue();
        }

        return tradingDeadlineType.name();
    }

    //TODO change response
    private String getPaymentMethod(final Auction auction) {
        var paymentMethod = auction.getPaymentMethod();
        if (PaymentMethod.OTHER == paymentMethod) {
            return auction.getPaymentMethodValue();
        }

        return paymentMethod.name();
    }
}
