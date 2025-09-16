package ru.practicum.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.EndpointHit;
import ru.practicum.mapper.StatsMapper;
import ru.practicum.repository.StatsRepository;
import ru.practicum.ViewStats;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class StatsServiceImpl implements StatsService {

    private final StatsRepository statsRepository;
    private final StatsMapper statsMapper;

    @Override
    public void saveHit(EndpointHit endpointHit) {
        statsRepository.save(statsMapper.toEntity(endpointHit));
    }

    @Override
    public List<ViewStats> getStats(LocalDateTime start, LocalDateTime end, List<String> uris, boolean unique) {
        if (uris == null || uris.isEmpty()) {
            if (unique) {
                return statsRepository.findUniqueStatsWithoutUris(start, end);
            } else {
                return statsRepository.findStatsWithoutUris(start, end);
            }
        } else {
            if (unique) {
                return statsRepository.findUniqueStatsWithUris(start, end, uris);
            } else {
                return statsRepository.findStatsWithUris(start, end, uris);
            }
        }
    }
}
