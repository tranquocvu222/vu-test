package com.nals.auction.api.v1;

import com.nals.auction.bloc.ProductCrudBloc;
import com.nals.auction.dto.request.product.ProductCreateReq;
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
@RequestMapping("/api/v1/products")
public class ProductCrudController
    extends BaseController {

    private final ProductCrudBloc productCrudBloc;

    public ProductCrudController(final Validator validator, final ProductCrudBloc productCrudBloc) {
        super(validator);
        this.productCrudBloc = productCrudBloc;
    }

    @PostMapping
    @PreAuthorize("hasRole('SELLER')")
    public ResponseEntity<?> createProduct(@Valid @RequestBody final ProductCreateReq req) {
        return created(productCrudBloc.createProduct(req));
    }
}
