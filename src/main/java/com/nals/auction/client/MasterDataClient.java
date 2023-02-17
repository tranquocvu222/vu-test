package com.nals.auction.client;

import com.nals.auction.config.ApplicationProperties;
import com.nals.auction.dto.CertificationDto;
import com.nals.auction.dto.LocationDto;
import com.nals.auction.dto.request.CertificationReq;
import com.nals.auction.exception.ExceptionHandler;
import com.nals.common.messages.errors.ValidatorException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.List;

import static com.nals.auction.exception.ExceptionHandler.NOT_FOUND;
import static org.springframework.http.MediaType.APPLICATION_JSON;

@Slf4j
@Component
public class MasterDataClient {

    private static final String MASTER_DATA_URI_PREFIX = "master-data";
    private static final String TOWN_URI_PREFIX = "towns";
    private static final String LOCATION_URI_PREFIX = "locations";
    private static final String CERTIFICATION_URI_PREFIX = "certifications";

    private final RestTemplate restTemplate;
    private final ExceptionHandler exceptionHandler;
    private final String masterDataUrl;

    public MasterDataClient(final RestTemplate restTemplate,
                            final ExceptionHandler exceptionHandler,
                            final ApplicationProperties applicationProperties) {
        this.exceptionHandler = exceptionHandler;
        this.restTemplate = restTemplate;
        this.masterDataUrl = applicationProperties.getMasterDataUrl();
    }

    public CertificationDto getCertificationsByName(final String name) {
        log.info("Get certification by name #{}", name);
        var headers = new HttpHeaders();
        headers.setContentType(APPLICATION_JSON);
        var url = String.format("%s/%s/%s/%s", masterDataUrl, MASTER_DATA_URI_PREFIX, CERTIFICATION_URI_PREFIX, name);
        var request = new HttpEntity<>(headers);

        ResponseEntity<CertificationDto> response;
        try {
            response = restTemplate.exchange(url, HttpMethod.GET, request, CertificationDto.class);
            return response.getBody();
        } catch (Exception exception) {
            throw new ValidatorException("certificate",
                                         exceptionHandler.getMessageCode(NOT_FOUND),
                                         exceptionHandler.getMessageContent(NOT_FOUND));
        }
    }

    public Boolean existedTownById(final Long id) {
        log.info("Existed town by id #{}", id);
        var headers = new HttpHeaders();
        headers.setContentType(APPLICATION_JSON);
        var url = String.format("%s/%s/%s/%s/%s", masterDataUrl, MASTER_DATA_URI_PREFIX,
                                TOWN_URI_PREFIX, id.toString(), "exists");
        var request = new HttpEntity<>(headers);

        try {
            var response = restTemplate.exchange(url, HttpMethod.GET, request, Boolean.class);
            return response.getBody();
        } catch (Exception exception) {
            throw new ValidatorException("town_id",
                                         exceptionHandler.getMessageCode(NOT_FOUND),
                                         exceptionHandler.getMessageContent(NOT_FOUND));
        }
    }

    public Boolean existedCertificationsByIdIn(final List<Long> certificateIds) {
        log.info("Existed certification by ids in #{}", certificateIds);
        var headers = new HttpHeaders();
        headers.setContentType(APPLICATION_JSON);
        var url = String.format("%s/%s/%s/%s", masterDataUrl, MASTER_DATA_URI_PREFIX, CERTIFICATION_URI_PREFIX, "exists");
        var req = CertificationReq.builder()
                                  .ids(certificateIds)
                                  .build();
        var request = new HttpEntity<>(req, headers);

        try {
            var response = restTemplate.postForEntity(url, request, Boolean.class);
            return response.getBody();
        } catch (Exception exception) {
            throw new ValidatorException("certificate_id",
                                         exceptionHandler.getMessageCode(NOT_FOUND),
                                         exceptionHandler.getMessageContent(NOT_FOUND));
        }
    }

    public LocationDto getLocationRes(final Long townId) {
        log.info("Get location with townId #{}", townId);
        var headers = new HttpHeaders();
        headers.setContentType(APPLICATION_JSON);
        var url = String.format("%s/%s/%s/%s", masterDataUrl, MASTER_DATA_URI_PREFIX,
                                LOCATION_URI_PREFIX, townId.toString());
        var request = new HttpEntity<>(headers);

        try {
            var response = restTemplate.exchange(url, HttpMethod.GET, request, LocationDto.class);
            return response.getBody();
        } catch (Exception exception) {
            throw new ValidatorException(exceptionHandler.getMessageCode(NOT_FOUND),
                                         exceptionHandler.getMessageContent(NOT_FOUND));
        }
    }
}
