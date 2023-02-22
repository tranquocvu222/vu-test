package com.nals.auction.repository;

import com.nals.auction.domain.Media;
import com.nals.utils.enums.MediaType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Repository
public interface MediaRepository
    extends JpaRepository<Media, Long> {

    @Query("SELECT new Media(m.sourceId, m.name, m.type)"
        + " FROM Media m"
        + " WHERE m.sourceId IN :sourceIds"
        + " AND (m.type IN :types)"
        + " ORDER BY m.id ASC")
    List<Media> fetchBySourceId(@Param("sourceIds") Collection<Long> sourceIds,
                                @Param("types") Collection<MediaType> types);

    Optional<Media> findBySourceIdAndType(Long sourceId, MediaType type);
}
