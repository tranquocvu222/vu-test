package com.nals.auction.security.jwt;

import com.nals.auction.config.ApplicationProperties;
import com.nals.common.messages.errors.InvalidTokenException;
import com.nals.utils.domain.DomainUserDetails;
import com.nals.utils.dto.response.AuthenticationRes;
import com.nals.utils.dto.response.DataRes;
import com.nals.utils.helpers.JsonHelper;
import io.jsonwebtoken.JwtException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.util.Strings;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.web.reactive.function.client.WebClient;

import javax.servlet.http.HttpServletRequest;

import java.util.Collection;
import java.util.Collections;
import java.util.stream.Collectors;

@Slf4j
@Component
public final class TokenProvider {
    public static final String AUTHORIZATION_HEADER = "RMT-Authorization";
    private final WebClient.Builder webClientBuilder;

    private String uaaBaseUri;

    public TokenProvider(final WebClient.Builder webClientBuilder,
                         final ApplicationProperties applicationProperties) {
        this.webClientBuilder = webClientBuilder;
        this.uaaBaseUri = applicationProperties.getUaaBaseUri();
    }

    public String resolveToken(final HttpServletRequest request) {
        String bearerToken = request.getHeader(AUTHORIZATION_HEADER);
        if (StringUtils.isNotBlank(bearerToken) && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }

    public Authentication validateAndGetAuthentication(final String token) {
        if (Strings.isBlank(token)) {
            return null;
        }

        try {
            log.debug("Calling UAA to validate token");

            var response = webClientBuilder.build()
                                           .post()
                                           .uri(String.format(uaaBaseUri + "/token/verify?token=%s", token))
                                           .retrieve()
                                           .bodyToMono(DataRes.class)
                                           .block();
            if (response == null) {
                throw new InvalidTokenException("", "");
//                ExceptionHandler.throwException(com.nals.common.messages.errors.InvalidTokenException.class,
//                ExceptionHandler.INVALID_TOKEN);
            }

            var authenticationRes = JsonHelper.convertValue(response.getData(), AuthenticationRes.class);

            var roles = convertToGrantedAuthority(authenticationRes.getRoles());
            var permissions = convertToGrantedAuthority(authenticationRes.getPermissions());
            DomainUserDetails principal = DomainUserDetails.builder()
                                                           .id(authenticationRes.getUserId())
                                                           .username(authenticationRes.getUsername())
                                                           .roles(roles)
                                                           .authorities(permissions)
                                                           .build();

            return new UsernamePasswordAuthenticationToken(principal, token, permissions);
        } catch (JwtException | IllegalArgumentException e) {
            log.warn("Invalid JWT token trace: {}", e.getMessage());
            return null;
        }
    }

    private Collection<? extends GrantedAuthority> convertToGrantedAuthority(final Collection<String> authorities) {
        if (CollectionUtils.isEmpty(authorities)) {
            return Collections.emptyList();
        }

        return authorities.stream()
                          .map(SimpleGrantedAuthority::new)
                          .collect(Collectors.toList());
    }
}
