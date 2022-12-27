package com.nals.auction.security;

import com.nals.utils.helpers.SecurityHelper;
import org.springframework.data.domain.AuditorAware;
import org.springframework.stereotype.Component;

import java.util.Optional;

import static com.nals.utils.constants.Constants.SYSTEM;

/**
 * Implementation of {@link AuditorAware} based on Spring Security.
 */
@Component
public class SpringSecurityAuditorAware
    implements AuditorAware<String> {

    @Override
    public Optional<String> getCurrentAuditor() {
        String username = SecurityHelper.getCurrentUserLogin().orElse(SYSTEM);
        return Optional.of(username);
    }
}
