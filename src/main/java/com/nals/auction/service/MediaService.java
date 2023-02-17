package com.nals.auction.service;

import com.nals.auction.domain.Media;
import com.nals.auction.repository.MediaRepository;
import com.nals.utils.service.BaseService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class MediaService
    extends BaseService<Media, MediaRepository> {

    public MediaService(final MediaRepository repository) {
        super(repository);
    }
}
