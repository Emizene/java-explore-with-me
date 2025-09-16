package ru.practicum;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

import static ru.practicum.Constants.DATE_TIME_PATTERN;

@Slf4j
@RequiredArgsConstructor
public class StatsClientImpl implements StatsClient {

    private final RestTemplate restTemplate;
    private final String serverUrl;

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern(DATE_TIME_PATTERN);

    @Override
    public void saveHit(EndpointHit endpointHit) {
        try {
            ResponseEntity<Void> response = restTemplate.postForEntity(
                    serverUrl + "/hit",
                    endpointHit,
                    Void.class
            );

            if (response.getStatusCode().is2xxSuccessful()) {
                log.debug("Hit successfully saved: {}", endpointHit);
            }
        } catch (Exception e) {
            log.error("Failed to save hit to stats service: {}", e.getMessage());
        }
    }

    @Override
    public List<ViewStats> getStats(LocalDateTime start, LocalDateTime end,
                                    List<String> uris, Boolean unique) {
        try {
            String encodedStart = encodeDateTime(start);
            String encodedEnd = encodeDateTime(end);
            boolean uniqueFlag = unique != null ? unique : false;

            StringBuilder urlBuilder = new StringBuilder(serverUrl)
                    .append("/stats?start=").append(encodedStart)
                    .append("&end=").append(encodedEnd)
                    .append("&unique=").append(uniqueFlag);

            if (uris != null && !uris.isEmpty()) {
                for (String uri : uris) {
                    urlBuilder.append("&uris=").append(encodeUri(uri));
                }
            }

            ResponseEntity<ViewStats[]> response = restTemplate.getForEntity(
                    urlBuilder.toString(),
                    ViewStats[].class
            );

            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                return Arrays.asList(response.getBody());
            }
        } catch (Exception e) {
            log.error("Failed to get stats from stats service: {}", e.getMessage());
        }

        return List.of();
    }

    private String encodeDateTime(LocalDateTime dateTime) {
        return URLEncoder.encode(dateTime.format(FORMATTER), StandardCharsets.UTF_8);
    }

    private String encodeUri(String uri) {
        return URLEncoder.encode(uri, StandardCharsets.UTF_8);
    }
}