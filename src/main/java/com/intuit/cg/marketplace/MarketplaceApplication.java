package com.intuit.cg.marketplace;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Import;
import org.springframework.data.rest.webmvc.config.RepositoryRestMvcConfiguration;

@SpringBootApplication
//Following annotation is needed so that I can post uris to link entities e.g. posting seller:http://localhost:8080/projects/1/seller when creating a project
@Import(RepositoryRestMvcConfiguration.class)
public class MarketplaceApplication {
    public static void main(String[] args) {
        SpringApplication.run(MarketplaceApplication.class, args);
    }
}
