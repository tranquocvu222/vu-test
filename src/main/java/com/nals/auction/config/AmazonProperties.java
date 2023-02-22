package com.nals.auction.config;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties(prefix = "amazon", ignoreUnknownFields = false)
public class AmazonProperties {

    private AmazonS3 s3;
    private CloudFront cloudFront;
    private String accessKey;
    private String secretKey;
    private String region;

    @Data
    public static class AmazonS3 {
        private String bucketName = "";
        private String tempDir = "";
        private String workingDir = "";
        private boolean useIamRole = false;
    }

    @Getter
    @Setter
    public static class CloudFront {
        private String endpointUrl = "";
    }
}
