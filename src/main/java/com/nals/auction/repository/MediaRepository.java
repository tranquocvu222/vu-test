package com.nals.auction.repository;

import com.nals.auction.domain.Media;
import com.nals.utils.enums.MediaType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Repository
public interface MediaRepository
    extends JpaRepository<Media, Long> {

    boolean existsBySourceIdAndType(final Long id, final MediaType mediaType);

    Optional<Media> findFirstBySourceIdAndType(final Long sourceId, final MediaType type);

    Optional<Media> findBySourceIdAndType(final Long sourceId, final MediaType type);

    List<Media> findAllBySourceIdInAndTypeIn(final Collection<Long> sourceIds, final Collection<MediaType> types);

    List<Media> findAllBySourceIdAndTypeIn(final Long sourceId, final Collection<MediaType> types);

    void deleteBySourceIdAndTypeIn(final Long sourceId, final Collection<MediaType> types);

    void deleteBySourceIdAndNameInAndTypeIn(final Long sourceId,
                                            final Collection<String> imageNames,
                                            final Collection<MediaType> types);
}