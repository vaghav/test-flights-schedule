package com.ryanair.flights.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

@Configuration
@EnableWebMvc
@PropertySource("classpath:/flights.properties")
@ComponentScan(basePackages = "com.ryanair.flights")
public class RootConfiguration {

    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }
}