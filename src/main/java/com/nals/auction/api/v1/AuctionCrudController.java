package com.nals.auction.api.v1;

import com.nals.auction.bloc.AuctionCrudBloc;
import com.nals.auction.dto.request.AuctionReq;
import com.nals.utils.controller.BaseController;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import javax.validation.Validator;

@RestController
@RequestMapping("/api/v1/auctions")
public class AuctionCrudController
    extends BaseController {

    private final AuctionCrudBloc auctionCrudBloc;

    public AuctionCrudController(final Validator validator,
                                 final AuctionCrudBloc auctionCrudBloc) {
        super(validator);
        this.auctionCrudBloc = auctionCrudBloc;
    }

    @PostMapping
    @PreAuthorize("hasRole('SELLER')")
    public ResponseEntity<?> createAuction(@Valid @RequestBody final AuctionReq req) {
        return created(auctionCrudBloc.createAuction(req));
    }
}
