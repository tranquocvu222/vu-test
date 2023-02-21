package com.nals.auction.dto.response;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.nals.utils.enums.AuctionStatus;
import com.nals.utils.enums.DeliveryDateType;
import com.nals.utils.enums.LogisticsArrangementType;
import com.nals.utils.enums.PaymentMethod;
import com.nals.utils.enums.TradingDeadlineType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class AuctionRes {

    private Long id;
    private String startTime;
    private String endTime;
    private BigDecimal startPrice;
    private Long quantity;
    private AuctionStatus status;
    private DeliveryDateType deliveryDateType;
    private PaymentMethod paymentMethod;
    private String prefectureNameEn;
    private String prefectureNameJa;
    private String certificateNumber;
    private TradingDeadlineType tradingDeadlineType;
    private LogisticsArrangementType logisticsArrangementType;
    private ProductRes product;
    private List<MediaRes> images;
}
