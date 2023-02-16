package com.nals.auction.client;

import com.nals.auction.config.ApplicationProperties;
import com.nals.auction.dto.UserDto;
import com.nals.auction.dto.request.UserCompanyUpdateReq;
import com.nals.auction.exception.ExceptionHandler;
import com.nals.common.messages.errors.ObjectNotFoundException;
import com.nals.common.messages.errors.ValidatorException;
import com.nals.utils.helpers.SecurityHelper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import static com.nals.auction.exception.ExceptionHandler.NOT_FOUND;
import static com.nals.auction.exception.ExceptionHandler.OBJECT_NOT_FOUND;
import static org.springframework.http.MediaType.APPLICATION_JSON;

@Slf4j
@Component
public class UaaClient {

    private static final String UAA_URI_PREFIX = "uaa";
    private static final String USER_URI_PREFIX = "users";

    private final RestTemplate restTemplate;
    private final ExceptionHandler exceptionHandler;
    private final String uaaUrl;

    public UaaClient(final RestTemplate restTemplate,
                     final ExceptionHandler exceptionHandler,
                     final ApplicationProperties applicationProperties) {
        this.exceptionHandler = exceptionHandler;
        this.restTemplate = restTemplate;
        this.uaaUrl = applicationProperties.getUaaUrl();
    }

    public UserDto getCurrentUser() {
        var currentUserId = SecurityHelper.getCurrentUserId();
        log.info("Get current user with id #{}", currentUserId);
        var headers = new HttpHeaders();
        headers.setContentType(APPLICATION_JSON);
        var url = String.format("%s/%s/%s/%s", uaaUrl, UAA_URI_PREFIX, USER_URI_PREFIX, currentUserId.toString());
        var request = new HttpEntity<>(headers);

        try {
            var response = restTemplate.exchange(url, HttpMethod.GET, request, UserDto.class);
            return response.getBody();
        } catch (Exception exception) {
            throw new ObjectNotFoundException(exceptionHandler.getMessageCode(OBJECT_NOT_FOUND),
                                              exceptionHandler.getMessageContent(OBJECT_NOT_FOUND));
        }
    }

    public void attachCompanyToUser(final Long companyId, final Long userId) {
        log.info("Attach company with companyId #{} to user #{}", companyId, userId);
        var headers = new HttpHeaders();
        headers.setContentType(APPLICATION_JSON);
        var url = String.format("%s/%s/%s", uaaUrl, UAA_URI_PREFIX, USER_URI_PREFIX);
        var req = UserCompanyUpdateReq.builder()
                                      .id(userId)
                                      .companyId(companyId)
                                      .build();
        var request = new HttpEntity<>(req, headers);

        try {
            restTemplate.exchange(url, HttpMethod.PUT, request, String.class);
        } catch (Exception exception) {
            throw new ValidatorException("user",
                                         exceptionHandler.getMessageCode(NOT_FOUND),
                                         exceptionHandler.getMessageContent(NOT_FOUND));
        }
    }
}
