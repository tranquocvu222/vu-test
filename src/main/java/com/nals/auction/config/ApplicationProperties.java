package com.nals.auction.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.HashSet;
import java.util.Set;

/**
 * Properties specific to RMT Auction
 * <p>
 * Properties are configured in the {@code application.yml} file.
 * See {@link tech.jhipster.config.JHipsterProperties} for a good example.
 */
@Getter
@Setter
@ConfigurationProperties(prefix = "application", ignoreUnknownFields = false)
public class ApplicationProperties {

    private FileUpload fileUpload;
    private String masterDataUrl;
    private String uaaUrl;

    @Getter
    @Setter
    public static class FileUpload {
        private int maxSizeAllow = 10240;
        private Set<String> allowExtensions = new HashSet<>();
    }
}
