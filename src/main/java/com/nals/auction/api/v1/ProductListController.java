package com.nals.auction.api.v1;

import com.nals.auction.bloc.ProductListBloc;
import com.nals.utils.controller.BaseController;
import org.springframework.web.bind.annotation.RequestMapping;
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
}
