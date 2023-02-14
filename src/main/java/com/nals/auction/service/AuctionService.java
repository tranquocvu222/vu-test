package com.nals.auction.service;

import com.nals.auction.domain.Auction;
import com.nals.auction.repository.AuctionRepository;
import com.nals.utils.service.BaseService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class AuctionService
    extends BaseService<Auction, AuctionRepository> {

    public AuctionService(final AuctionRepository repository) {
        super(repository);
    }
}
