package com.nals.auction.api.v1;

import com.nals.auction.bloc.ProductListBloc;
import com.nals.utils.controller.BaseController;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Validator;

@RestController
@RequestMapping("/api/v1/products")
public class ProductListController
    extends BaseController {

    private final ProductListBloc productListBloc;

    public ProductListController(final Validator validator, final ProductListBloc productListBloc) {
        super(validator);
        this.productListBloc = productListBloc;
    }

    @GetMapping
    @PreAuthorize("hasRole('SELLER')")
    public ResponseEntity<?> searchProducts(@RequestParam(required = false) final String name,
                                            @RequestParam(defaultValue = "0") int page,
                                            @RequestParam(value = "per_page", defaultValue = "10") int perPage) {
        return ok(productListBloc.searchProducts(name, page, perPage));
    }
}
