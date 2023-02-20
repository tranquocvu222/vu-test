package com.nals.auction.service;

import com.nals.auction.domain.Product;
import com.nals.auction.repository.ProductRepository;
import com.nals.utils.service.BaseService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Slf4j
@Service
public class ProductService
    extends BaseService<Product, ProductRepository> {

    public ProductService(final ProductRepository repository) {
        super(repository);
    }

    public Optional<Product> getByIdAndCompanyId(final Long id, final Long companyId) {
        log.info("Get product by id #{} and company id #{}", id, companyId);
        return getRepository().findByIdAndCompanyId(id, companyId);
    }

    public Page<Product> searchProducts(final String name,
                                        final Long companyId,
                                        final PageRequest pageRequest) {
        log.info("Search products with name #{}", name);

        return getRepository().searchProducts(name, companyId, pageRequest);
    }
}