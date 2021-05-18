package com.bnpinnovation.turret.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Configuration
public class UtilConfig {
    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }

}
