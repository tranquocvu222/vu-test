package com.nals.auction.repository;

import com.nals.auction.domain.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductRepository
    extends JpaRepository<Product, Long> {

    @Query("SELECT DISTINCT new Product(p.id, p.name, p.varietyName, p.prefectureId, p.productionYear,"
        + "                             p.certificateNumber, p.moisture, m.name)"
        + " FROM Product p"
        + " LEFT JOIN Media m ON m.sourceId = p.id"
        + " WHERE (m.sourceId IS NULL OR m.type = 'PRODUCT_THUMBNAIL')"
        + "   AND p.company.id = :companyId"
        + "   AND (:name IS NULL OR p.name LIKE %:name%)")
    Page<Product> searchProducts(@Param("name") String name,
                                 @Param("companyId") Long companyId,
                                 Pageable pageable);
}
