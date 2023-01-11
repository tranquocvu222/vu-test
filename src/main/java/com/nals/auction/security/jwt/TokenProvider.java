package com.nals.auction.security.jwt;

import com.nals.utils.domain.DomainUserDetails;
import com.nals.utils.helpers.StringHelper;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.JwtParser;
import io.jsonwebtoken.Jwts;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.util.Strings;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.stream.Collectors;

@Slf4j
@Component
public final class TokenProvider {
    public static final String AUTHORIZATION_HEADER = "RMT-Authorization";

    public static final String ROLES_KEY = "roles";
    public static final String PERMISSIONS_KEY = "perms";
    public static final String USER_ID_KEY = "user_id";

    public static final String COMMA = ",";
    public static final String SIGNATURE_PATTERN = "[^.]*$";

    private final JwtParser jwtParser;

    public TokenProvider() {
        this.jwtParser = Jwts.parserBuilder().build();
    }

    public String resolveToken(final HttpServletRequest request) {
        String bearerToken = request.getHeader(AUTHORIZATION_HEADER);
        if (StringUtils.isNotBlank(bearerToken) && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }

    public Authentication getAuthentication(final String token) {
        if (Strings.isBlank(token)) {
            return null;
        }

        try {
            log.debug("Check token is valid or not");

            // Parse and check token is expired or not
            Claims claims = parseClaimsJwt(token);

            var roles = convertToGrantedAuthorities((String) claims.getOrDefault(ROLES_KEY, ""));
            var permissions = convertToGrantedAuthorities((String) claims.getOrDefault(PERMISSIONS_KEY, ""));
            DomainUserDetails principal = DomainUserDetails.builder()
                                                           .id(claims.get(USER_ID_KEY, Long.class))
                                                           .username(claims.getSubject())
                                                           .roles(roles)
                                                           .authorities(permissions)
                                                           .build();

            return new UsernamePasswordAuthenticationToken(principal, token, permissions);
        } catch (JwtException | IllegalArgumentException e) {
            log.warn("Invalid JWT token trace: {}", e.getMessage());
            return null;
        }
    }

    public Claims parseClaimsJwt(final String token) {
        return jwtParser.parseClaimsJwt(token.replaceFirst(SIGNATURE_PATTERN, ""))
                        .getBody();
    }

    private Collection<? extends GrantedAuthority> convertToGrantedAuthorities(final String authorities) {
        if (StringHelper.isBlank(authorities)) {
            return Collections.emptyList();
        }
        return Arrays.stream(authorities.split(COMMA))
                     .map(SimpleGrantedAuthority::new)
                     .collect(Collectors.toList());
    }
}
