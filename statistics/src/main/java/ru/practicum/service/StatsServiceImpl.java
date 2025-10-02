package ru.practicum.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.EndpointHit;
import ru.practicum.ViewStats;
import ru.practicum.exception.BadRequestException;
import ru.practicum.mapper.StatsMapper;
import ru.practicum.repository.StatsRepository;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class StatsServiceImpl implements StatsService {

    private final StatsRepository statsRepository;
    private final StatsMapper statsMapper;

    @Transactional
    @Override
    public void saveHit(EndpointHit endpointHit) {
        statsRepository.save(statsMapper.toEntity(endpointHit));
    }

    @Override
    public List<ViewStats> getStats(LocalDateTime start, LocalDateTime end, List<String> uris, boolean unique) {

        if (start != null && end != null && start.isAfter(end)) {
            throw new BadRequestException("Start date must be before end date");
        }

        if (unique) {
            return statsRepository.findUniqueStats(start, end, uris);
        } else {
            return statsRepository.findStats(start, end, uris);
        }
    }
}
