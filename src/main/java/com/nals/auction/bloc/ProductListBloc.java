package com.nals.auction.bloc;

import com.nals.auction.client.MasterDataClient;
import com.nals.auction.client.UaaClient;
import com.nals.auction.domain.Product;
import com.nals.auction.dto.ProductSearchRes;
import com.nals.auction.dto.response.prefecture.PrefectureRes;
import com.nals.auction.mapper.MapperHelper;
import com.nals.auction.service.ProductService;
import com.nals.auction.service.StorageService;
import com.nals.utils.dto.request.SearchReq;
import com.nals.utils.helpers.PaginationHelper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProductListBloc {

    private final ProductService productService;
    private final StorageService storageService;
    private final UaaClient uaaClient;
    private final MasterDataClient masterDataClient;

    @Transactional(readOnly = true)
    public Page<ProductSearchRes> searchProducts(final String name, final int page, final int perPage) {
        log.info("Search product with name #{} and page #{} and perPage #{}", name, page, perPage);

        var companyId = uaaClient.getCurrentUser().getCompanyId();

        if (Objects.isNull(companyId)) {
            return null;
        }

        var searchReq = SearchReq.builder()
                                 .page(page)
                                 .perPage(perPage)
                                 .build();
        var products = productService.searchProducts(name,
                                                     companyId,
                                                     PaginationHelper.generatePageRequest(searchReq));
        var prefectureIds = products.stream()
                                    .map(Product::getPrefectureId)
                                    .collect(Collectors.toList());

        //TODO using cache for get this data
        var prefectureResMap = masterDataClient
                .fetchPrefectureRes(prefectureIds)
                .stream()
                .collect(Collectors.toMap(PrefectureRes::getId, Function.identity()));

        return products.map(product -> {
            var res = MapperHelper.INSTANCE.toProductSearchRes(product);
            var prefectureRes = prefectureResMap.get(product.getPrefectureId());

            res.setImageUrl(storageService.getFullFileUrl(product.getMedia().getName()));
            res.setProductionAreaEn(prefectureRes.getNameEn());
            res.setProductionAreaJa(prefectureRes.getNameJa());

            return res;
        });
    }
}
