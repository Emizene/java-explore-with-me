package ru.practicum;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties("stats-client")
public class StatsClientProperties {
    private String url;
}
