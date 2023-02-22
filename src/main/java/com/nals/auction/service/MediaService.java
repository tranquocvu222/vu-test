package com.nals.auction.service;

import com.nals.auction.domain.Media;
import com.nals.auction.repository.MediaRepository;
import com.nals.utils.enums.MediaType;
import com.nals.utils.service.BaseService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
public class MediaService
    extends BaseService<Media, MediaRepository> {

    public MediaService(final MediaRepository repository) {
        super(repository);
    }

    public boolean existsBySourceIdAndType(final Long sourceId, final MediaType mediaType) {
        log.info("Check exist media by sourceId #{} and type #{}", sourceId, mediaType);
        return getRepository().existsBySourceIdAndType(sourceId, mediaType);
    }

    public Optional<Media> findFirstBySourceIdAndType(final Long sourceId, final MediaType type) {
        log.info("Find first media by sourceId #{} and type #{}", sourceId, type);

        return getRepository().findFirstBySourceIdAndType(sourceId, type);
    }

    public List<Media> fetchBySourceIdsAndTypes(final Collection<Long> sourceIds,
                                                final Collection<MediaType> mediaTypes) {
        log.info("Fetch media in sourceIds #{} and mediaTypes #{}", sourceIds, mediaTypes);

        if (CollectionUtils.isEmpty(mediaTypes) || CollectionUtils.isEmpty(sourceIds)) {
            return Collections.emptyList();
        }

        return getRepository().findAllBySourceIdInAndTypeIn(sourceIds, mediaTypes);
    }

    public Optional<Media> getBySourceIdAndType(final Long sourceId, final MediaType type) {
        log.info("Get media by source id #{} and type #{}", sourceId, type);
        return getRepository().findBySourceIdAndType(sourceId, type);
    }

    public List<Media> fetchBySourceIdAndTypes(final Long sourceId,
                                               final Collection<MediaType> mediaTypes) {
        log.info("Fetch media in sourceId #{} and mediaTypes #{}", sourceId, mediaTypes);

        if (CollectionUtils.isEmpty(mediaTypes)) {
            return Collections.emptyList();
        }

        return getRepository().findAllBySourceIdAndTypeIn(sourceId, mediaTypes);
    }

    @Transactional
    public void deleteBySourceIdAndTypes(final Long sourceId, final Collection<MediaType> types) {
        log.info("Delete media by source id #{} and types #{}", sourceId, types);

        if (CollectionUtils.isEmpty(types)) {
            Collections.emptyList();
        }

        getRepository().deleteBySourceIdAndTypeIn(sourceId, types);
    }

    @Transactional
    public void deleteBySourceIdAndNamesAndTypes(final Long sourceId,
                                                 Collection<String> imageNames,
                                                 final Collection<MediaType> types) {
        log.info("Delete media by sourceId #{} and types #{} and imageNames #{}", sourceId, types, imageNames);

        if (CollectionUtils.isEmpty(imageNames) || CollectionUtils.isEmpty(types)) {
            Collections.emptyList();
        }

        getRepository().deleteBySourceIdAndNameInAndTypeIn(sourceId, imageNames, types);
    }
}