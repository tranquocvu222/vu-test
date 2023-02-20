package com.nals.auction.api.v1;

import com.nals.auction.bloc.ProductCrudBloc;
import com.nals.auction.dto.request.ProductReq;
import com.nals.utils.controller.BaseController;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.PutMapping;
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
    public ResponseEntity<?> createProduct(@Valid @RequestBody final ProductReq req) {
        return created(productCrudBloc.createProduct(req));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('SELLER')")
    public ResponseEntity<?> updateProduct(@PathVariable final Long id,
                                           @Valid @RequestBody final ProductReq req) {
        productCrudBloc.updateProduct(id, req);
        return noContent();
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('SELLER')")
    public ResponseEntity<?> getProductDetail(@PathVariable final Long id) {
        return ok(productCrudBloc.getProductById(id));
    }
}