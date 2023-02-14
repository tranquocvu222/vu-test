package com.nals.auction.repository;

import com.nals.auction.domain.Auction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AuctionRepository
    extends JpaRepository<Auction, Long> {
}
