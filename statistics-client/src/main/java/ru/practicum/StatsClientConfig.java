package ru.practicum;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@EnableConfigurationProperties(StatsClientProperties.class)
@Configuration
public class StatsClientConfig {

    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }

    @Bean
    public StatsClient statsClient(RestTemplate restTemplate, StatsClientProperties statsClientProperties) {
        return new StatsClientImpl(restTemplate, statsClientProperties
                .getUrl());
    }
}
