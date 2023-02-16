package com.nals.auction.domain;

import com.nals.utils.enums.AuctionStatus;
import com.nals.utils.enums.DeliveryDateType;
import com.nals.utils.enums.LogisticsArrangementType;
import com.nals.utils.enums.PaymentMethod;
import com.nals.utils.enums.TradingDeadlineType;
import lombok.AllArgsConstructor;
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
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import java.math.BigDecimal;
import java.time.Instant;

import static javax.persistence.EnumType.STRING;

@Entity
@Getter
@Setter
@ToString
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "auctions")
public class Auction
    extends AbstractAuditingEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "product_name", nullable = false)
    private String productName;

    @Column(name = "variety_name", nullable = false)
    private String varietyName;

    @Column(name = "product_area", nullable = false)
    private String productionArea;

    @Column(name = "production_year", nullable = false)
    private String productionYear;

    @Column(nullable = false)
    private Double moisture;

    @Column
    private String certificate;

    @Column(name = "start_time", nullable = false)
    private Instant startTime;

    @Column(name = "end_time", nullable = false)
    private Instant endTime;

    @Column(nullable = false)
    private Long quantity;

    @Column(name = "start_price", nullable = false)
    private BigDecimal startPrice;

    @Column(name = "prefecture_id")
    private Long prefectureId;

    @Enumerated(STRING)
    @Column(nullable = false)
    private AuctionStatus status;

    @Enumerated(STRING)
    @Column(name = "delivery_date_type")
    private DeliveryDateType deliveryDateType;

    @Column(name = "delivery_date_value")
    private String deliveryDateValue;

    @Enumerated(STRING)
    @Column(name = "trading_deadline_type")
    private TradingDeadlineType tradingDeadlineType;

    @Column(name = "trading_deadline_value")
    private String tradingDeadlineValue;

    @Enumerated(STRING)
    @Column(name = "logistics_arrangement_type", nullable = false)
    private LogisticsArrangementType logisticsArrangementType;

    @Enumerated(STRING)
    @Column(name = "payment_method")
    private PaymentMethod paymentMethod;

    @Column(name = "payment_method_value")
    private String paymentMethodValue;

    @ToString.Exclude
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id")
    private Product product;

    @ToString.Exclude
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "company_id")
    private Company company;

    public Auction(final Long id, final Instant startTime, final Instant endTime, final BigDecimal startPrice,
                   final Long quantity, final AuctionStatus status, final DeliveryDateType deliveryDateType,
                   final String deliveryDateValue, final TradingDeadlineType tradingDeadlineType,
                   final String tradingDeadlineValue, final PaymentMethod paymentMethod,
                   final String paymentMethodValue, final Long prefectureId,
                   final LogisticsArrangementType logisticsArrangementType, final String certificate,
                   final Long productId, final String productName, final boolean specialCultivationMethod,
                   final boolean organicJasMethod, final boolean noPesticidesMethod,
                   final boolean otherProductionMethod) {
        this.id = id;
        this.startTime = startTime;
        this.endTime = endTime;
        this.startPrice = startPrice;
        this.quantity = quantity;
        this.status = status;
        this.deliveryDateType = deliveryDateType;
        this.deliveryDateValue = deliveryDateValue;
        this.tradingDeadlineType = tradingDeadlineType;
        this.tradingDeadlineValue = tradingDeadlineValue;
        this.paymentMethod = paymentMethod;
        this.paymentMethodValue = paymentMethodValue;
        this.prefectureId = prefectureId;
        this.certificate = certificate;
        this.logisticsArrangementType = logisticsArrangementType;
        this.product = Product.builder()
                              .id(productId)
                              .name(productName)
                              .specialCultivationMethod(specialCultivationMethod)
                              .organicJasMethod(organicJasMethod)
                              .noPesticidesMethod(noPesticidesMethod)
                              .otherProductionMethod(otherProductionMethod)
                              .build();
    }
}
