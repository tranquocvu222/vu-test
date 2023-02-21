package com.nals.auction.dto.request;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.nals.utils.enums.LogisticsArrangementType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class AuctionReq {
    private Long productId;
    private String endTime;
    private Long quantity;
    private BigDecimal startPrice;
    private Long prefectureId;
    private DeliveryDateReq deliveryDate;
    private TradingDeadlineReq tradingDeadline;
    private LogisticsArrangementType logisticsArrangementType;
    private PaymentMethodReq paymentMethod;
}
