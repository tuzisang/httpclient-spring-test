package com.electricity.httpclientspringtest;

import com.electricity.httpclientspringtest.config.HttpClientConfig;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@EnableConfigurationProperties(HttpClientConfig.class)
@SpringBootApplication
public class HttpclientSpringTestApplication {

    public static void main(String[] args) {
        SpringApplication.run(HttpclientSpringTestApplication.class, args);

    }


}
