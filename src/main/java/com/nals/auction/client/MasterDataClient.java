package com.nals.auction.client;

import com.nals.auction.client.InternalClientConfigFactory.InternalClient;
import com.nals.auction.dto.CertificationDto;
import com.nals.auction.dto.LocationDto;
import com.nals.auction.dto.request.CertificationReq;
import com.nals.auction.exception.ExceptionHandler;
import com.nals.common.messages.errors.ValidatorException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.List;

import static com.nals.auction.client.InternalClientConfigFactory.InternalClient.MASTER;
import static com.nals.auction.exception.ExceptionHandler.NOT_FOUND;

@Slf4j
@Component
public class MasterDataClient
    extends BaseClient {

    private static final String MASTER_DATA_URI_PREFIX = "master-data";
    private static final String TOWN_URI_PREFIX = "towns";
    private static final String LOCATION_URI_PREFIX = "locations";
    private static final String CERTIFICATION_URI_PREFIX = "certifications";

    private final ExceptionHandler exceptionHandler;

    public MasterDataClient(final RestTemplate restTemplate,
                            final InternalClientConfigFactory internalClientConfigFactory,
                            final ExceptionHandler exceptionHandler) {
        super(restTemplate, internalClientConfigFactory);
        this.exceptionHandler = exceptionHandler;
    }

    @Override
    protected InternalClient getInternalClient() {
        return MASTER;
    }

    public CertificationDto getCertificationsByName(final String name) {
        log.info("Get certification by name #{}", name);
        var url = String.format("%s/%s/%s/%s", getBaseUri(), MASTER_DATA_URI_PREFIX, CERTIFICATION_URI_PREFIX, name);

        try {
            var response = get(url, CertificationDto.class);
            return response.getBody();
        } catch (Exception exception) {
            throw new ValidatorException("certificate",
                                         exceptionHandler.getMessageCode(NOT_FOUND),
                                         exceptionHandler.getMessageContent(NOT_FOUND));
        }
    }

    public Boolean existedTownById(final Long id) {
        log.info("Existed town by id #{}", id);
        var url = String.format("%s/%s/%s/%s/%s", getBaseUri(), MASTER_DATA_URI_PREFIX,
                                       TOWN_URI_PREFIX, id.toString(), "exists");

        try {
            var response = get(url, Boolean.class);
            return response.getBody();
        } catch (Exception exception) {
            throw new ValidatorException("town_id",
                                         exceptionHandler.getMessageCode(NOT_FOUND),
                                         exceptionHandler.getMessageContent(NOT_FOUND));
        }
    }

    public Boolean existedCertificationsByIdIn(final List<Long> certificateIds) {
        log.info("Existed certification by ids in #{}", certificateIds);
        var url = String.format("%s/%s/%s/%s", getBaseUri(),
                                MASTER_DATA_URI_PREFIX, CERTIFICATION_URI_PREFIX, "exists");
        var req = CertificationReq.builder()
                                  .ids(certificateIds)
                                  .build();

        try {
            var response = post(url, req, Boolean.class);
            return response.getBody();
        } catch (Exception exception) {
            throw new ValidatorException("certificate_id",
                                         exceptionHandler.getMessageCode(NOT_FOUND),
                                         exceptionHandler.getMessageContent(NOT_FOUND));
        }
    }

    public LocationDto getLocationRes(final Long townId) {
        log.info("Get location with townId #{}", townId);
        var url = String.format("%s/%s/%s/%s", getBaseUri(), MASTER_DATA_URI_PREFIX,
                                LOCATION_URI_PREFIX, townId.toString());

        try {
            var response = get(url, LocationDto.class);
            return response.getBody();
        } catch (Exception exception) {
            throw new ValidatorException(exceptionHandler.getMessageCode(NOT_FOUND),
                                         exceptionHandler.getMessageContent(NOT_FOUND));
        }
    }
}
