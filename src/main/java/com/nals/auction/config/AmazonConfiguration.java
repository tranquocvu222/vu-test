package com.nals.auction.config;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Slf4j
@Configuration
public class AmazonConfiguration {

    private final AmazonProperties amazonProperties;

    public AmazonConfiguration(final AmazonProperties amazonProperties) {
        this.amazonProperties = amazonProperties;
    }

    @Bean
    public AmazonS3 amazonS3() {
        return AmazonS3ClientBuilder.standard()
                                    .withRegion(amazonProperties.getRegion())
                                    .withCredentials(new AWSStaticCredentialsProvider(awsCredentials()))
                                    .build();
    }

    private AWSCredentials awsCredentials() {
        return new BasicAWSCredentials(amazonProperties.getAccessKey(),
                                       amazonProperties.getSecretKey());
    }
}
