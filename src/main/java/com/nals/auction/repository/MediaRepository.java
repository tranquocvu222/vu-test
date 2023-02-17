package com.nals.auction.repository;

import com.nals.auction.domain.Media;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MediaRepository
    extends JpaRepository<Media, Long> {
}
