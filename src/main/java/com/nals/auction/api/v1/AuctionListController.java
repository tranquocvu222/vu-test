package com.nals.auction.api.v1;

import com.nals.auction.bloc.AuctionListBloc;
import com.nals.auction.dto.request.AuctionSearchReq;
import com.nals.utils.controller.BaseController;
import com.nals.utils.helpers.JsonHelper;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Validator;

import java.util.Map;

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

    @GetMapping
    @PreAuthorize("hasRole('SELLER')")
    public ResponseEntity<?> searchAuction(@RequestParam final Map<String, Object> searchParams) {
        AuctionSearchReq req = JsonHelper.MAPPER.convertValue(searchParams, AuctionSearchReq.class);
        return ok(auctionListBloc.fetchAuctions(req));
    }
}
