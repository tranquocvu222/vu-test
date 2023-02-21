package com.nals.auction.repository;

import com.nals.auction.domain.Auction;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface AuctionRepository
    extends JpaRepository<Auction, Long> {

    @Query("SELECT new Auction(a.id, a.startTime, a.endTime, a.startPrice, a.quantity, a.status, a.deliveryDateType,"
        + "                    a.tradingDeadlineType, a.paymentMethod, a.prefectureId, a.logisticsArrangementType,"
        + "                    a.certificate, p.id, p.name, p.specialCultivationMethod, p.organicJasMethod,"
        + "                    p.noPesticidesMethod, p.otherProductionMethod)"
        + " FROM Auction a"
        + " JOIN Product p ON p.id = a.product.id"
        + " WHERE a.userId = :userId"
        + " AND (:keyword IS NULL OR p.name LIKE %:keyword%)")
    Page<Auction> fetchAuctions(@Param("userId") Long userId,
                                @Param("keyword") String keyword,
                                Pageable pageable);
}
