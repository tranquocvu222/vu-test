package com.nals.auction.service;

import com.nals.auction.domain.Product;
import com.nals.auction.repository.ProductRepository;
import com.nals.utils.service.BaseService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class ProductService
    extends BaseService<Product, ProductRepository> {

    public ProductService(ProductRepository repository) {
        super(repository);
    }
}
