package com.nals.auction;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ConfigurationPropertiesScan
@ComponentScan(basePackages = "com.nals")
public class RmtAuctionApp {

    public static void main(String[] args) {
        SpringApplication.run(RmtAuctionApp.class, args);
    }
}
