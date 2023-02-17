package com.nals.auction.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties(prefix = "amazon", ignoreUnknownFields = false)
public class AmazonProperties {

    private AmazonS3 s3;
    private String accessKey;
    private String secretKey;
    private String region;

    @Data
    public static class AmazonS3 {
        private String bucketName = "";
        private String tempDir = "";
        private String workingDir = "";
        private boolean useIamRole = false;
        private String s3Url = "";
    }
}
