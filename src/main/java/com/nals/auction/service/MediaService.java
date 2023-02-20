package com.nals.auction.service;

import com.nals.auction.domain.Media;
import com.nals.auction.repository.MediaRepository;
import com.nals.utils.enums.MediaType;
import com.nals.utils.service.BaseService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

@Slf4j
@Service
public class MediaService
    extends BaseService<Media, MediaRepository> {

    public MediaService(final MediaRepository repository) {
        super(repository);
    }

    public List<Media> fetchBySourceId(final Collection<Long> sourceIds,
                                       final Collection<MediaType> mediaTypes) {
        log.info("Fetch media in sourceIds #{} and mediaTypes #{}", sourceIds, mediaTypes);

        if (CollectionUtils.isEmpty(mediaTypes) || CollectionUtils.isEmpty(sourceIds)) {
            return Collections.emptyList();
        }

        return getRepository().fetchBySourceId(sourceIds, mediaTypes);
    }
}
