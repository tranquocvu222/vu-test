package com.nals.auction.dto.response.auction;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.nals.auction.dto.response.prefecture.PrefectureRes;
import com.nals.utils.enums.AuctionStatus;
import com.nals.utils.enums.LogisticsArrangementType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.math.BigDecimal;

@Getter
@Setter
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class AuctionDetailRes {
    private Long id;
    private String productName;
    private String imageName;
    private String productImage;
    private String productCertificate;
    private String productionYear;
    private PrefectureRes productionArea;
    private String varietyName;
    private Double moisture;
    private PrefectureRes prefectureRes;
    private AuctionStatus status;
    private BigDecimal startPrice;
    private Long quantity;
    private String endTime;
    private String deliveryDateType;
    private String tradingDeadlineType;
    private LogisticsArrangementType logisticsArrangementType;
    private String paymentMethod;
}
