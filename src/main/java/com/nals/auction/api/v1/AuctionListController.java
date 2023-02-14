package com.nals.auction.api.v1;

import com.nals.auction.bloc.AuctionListBloc;
import com.nals.utils.controller.BaseController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Validator;

@RestController
@RequestMapping("/api/v1/auctions")
public class AuctionListController
    extends BaseController {

    private final AuctionListBloc auctionListBloc;

    public AuctionListController(final Validator validator,
                                 final AuctionListBloc auctionListBloc) {
        super(validator);
        this.auctionListBloc = auctionListBloc;
    }
}
