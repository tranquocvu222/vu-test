package jp.co.unixon.kousa;

import jp.co.unixon.kousa.config.ApplicationProperties;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.ComponentScan;

@Slf4j
@SpringBootApplication
@EnableConfigurationProperties(ApplicationProperties.class)
@ComponentScan(basePackages = "jp.co.unixon")
public class KousaAPIApplication {

    public static void main(String[] args) {
        SpringApplication.run(KousaAPIApplication.class, args);
    }
}
