package com.nals.auction.bloc;

import com.nals.auction.client.MasterDataClient;
import com.nals.auction.domain.Media;
import com.nals.auction.dto.request.AuctionSearchReq;
import com.nals.auction.dto.response.AuctionRes;
import com.nals.auction.dto.response.MediaRes;
import com.nals.auction.dto.response.prefecture.PrefectureRes;
import com.nals.auction.mapper.MapperHelper;
import com.nals.auction.service.AuctionService;
import com.nals.auction.service.MediaService;
import com.nals.auction.service.StorageService;
import com.nals.utils.enums.MediaType;
import com.nals.utils.helpers.PaginationHelper;
import com.nals.utils.helpers.SecurityHelper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.EnumSet;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

import static com.nals.utils.enums.MediaType.PRODUCT_IMAGE;
import static com.nals.utils.enums.MediaType.PRODUCT_THUMBNAIL;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuctionListBloc {

    private static final Set<MediaType> PRODUCT_MEDIA_TYPE = EnumSet.of(PRODUCT_THUMBNAIL, PRODUCT_IMAGE);

    private final MasterDataClient masterDataClient;
    private final AuctionService auctionService;
    private final MediaService mediaService;
    private final StorageService storageService;

    @Transactional(readOnly = true)
    public Page<AuctionRes> fetchAuctions(final AuctionSearchReq req) {
        var currentUserId = SecurityHelper.getCurrentUserId();
        log.info("Fetch auctions of userId #{} with params #{}", currentUserId, req);

        Set<Long> prefectureIds = new HashSet<>();
        var auctions = auctionService.fetchAuctions(currentUserId, req.getKeyword(),
                                                    PaginationHelper.generatePageRequest(req));
        var productIds = auctions.map(auction -> {
                                     prefectureIds.add(auction.getPrefectureId());
                                     return auction.getProduct().getId();
                                 })
                                 .stream()
                                 .collect(Collectors.toList());

        var mediaMap = mediaService.fetchBySourceIdsAndTypes(productIds, PRODUCT_MEDIA_TYPE)
                                   .stream()
                                   .map(this::toMediaRes)
                                   .collect(Collectors.groupingBy(MediaRes::getSourceId));

        //TODO using cache for get this data
        var prefectureResMap = masterDataClient.fetchPrefectureRes(prefectureIds)
                                               .stream()
                                               .collect(Collectors.toMap(PrefectureRes::getId, Function.identity()));

        return auctions.map(auction -> {
            var res = MapperHelper.INSTANCE.toAuctionRes(auction);
            var prefectureRes = prefectureResMap.get(auction.getPrefectureId());

            res.setImages(mediaMap.get(res.getId()));
            res.setPrefectureNameEn(prefectureRes.getNameEn());
            res.setPrefectureNameJa(prefectureRes.getNameJa());

            return res;
        });
    }

    private MediaRes toMediaRes(final Media media) {
        var imageName = media.getName();
        return MediaRes.builder()
                       .sourceId(media.getSourceId())
                       .imageName(imageName)
                       .imageUrl(storageService.getFullFileUrl(imageName))
                       .type(media.getType())
                       .build();
    }
}
