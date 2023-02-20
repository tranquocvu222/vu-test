package com.nals.auction.client;

import com.nals.auction.client.InternalClientConfigFactory.InternalClient;
import com.nals.auction.dto.UserDto;
import com.nals.auction.dto.request.UserCompanyUpdateReq;
import com.nals.auction.exception.ExceptionHandler;
import com.nals.common.messages.errors.ObjectNotFoundException;
import com.nals.common.messages.errors.ValidatorException;
import com.nals.utils.helpers.SecurityHelper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import static com.nals.auction.client.InternalClientConfigFactory.InternalClient.UAA;
import static com.nals.auction.exception.ExceptionHandler.NOT_FOUND;
import static com.nals.auction.exception.ExceptionHandler.OBJECT_NOT_FOUND;

@Slf4j
@Component
public class UaaClient
    extends BaseClient {

    private static final String USER_URI_PREFIX = "users";

    private final ExceptionHandler exceptionHandler;

    public UaaClient(final RestTemplate restTemplate,
                     final InternalClientConfigFactory internalClientConfigFactory,
                     final ExceptionHandler exceptionHandler) {
        super(restTemplate, internalClientConfigFactory);
        this.exceptionHandler = exceptionHandler;
    }

    @Override
    protected InternalClient getInternalClient() {
        return UAA;
    }

    public UserDto getCurrentUser() {
        var currentUserId = SecurityHelper.getCurrentUserId();
        log.info("Get current user with id #{}", currentUserId);

        var url = String.format("%s/%s/%s", getBaseUri(), USER_URI_PREFIX, currentUserId.toString());

        try {
            var response = get(url, UserDto.class);
            return response.getBody();
        } catch (Exception exception) {
            throw new ObjectNotFoundException(exceptionHandler.getMessageCode(OBJECT_NOT_FOUND),
                                              exceptionHandler.getMessageContent(OBJECT_NOT_FOUND));
        }
    }

    public void attachCompanyToUser(final Long companyId, final Long userId) {
        log.info("Attach company with companyId #{} to user #{}", companyId, userId);
        var url = String.format("%s/%s", getBaseUri(), USER_URI_PREFIX);
        var req = UserCompanyUpdateReq.builder()
                                      .id(userId)
                                      .companyId(companyId)
                                      .build();

        try {
            put(url, req, String.class);
        } catch (Exception exception) {
            throw new ValidatorException("user",
                                         exceptionHandler.getMessageCode(NOT_FOUND),
                                         exceptionHandler.getMessageContent(NOT_FOUND));
        }
    }
}
