package com.nals.auction.service;

import com.nals.auction.domain.Auction;
import com.nals.auction.repository.AuctionRepository;
import com.nals.utils.service.BaseService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class AuctionService
    extends BaseService<Auction, AuctionRepository> {

    public AuctionService(final AuctionRepository repository) {
        super(repository);
    }

    public Page<Auction> fetchAuctions(final Long userId,
                                       final String keyword,
                                       final PageRequest pageRequest) {
        log.info("Fetch auctions of user #{} with keyword #{} and pageRequest #{}", userId, keyword, pageRequest);
        return getRepository().fetchAuctions(userId, keyword, pageRequest);
    }
}
