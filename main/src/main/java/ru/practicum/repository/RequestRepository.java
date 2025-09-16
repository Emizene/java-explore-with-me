package ru.practicum.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.practicum.emuns.RequestStatus;
import ru.practicum.Request;

import java.util.List;
import java.util.Optional;

@Repository
public interface RequestRepository extends JpaRepository<Request, Long> {
    List<Request> findAllByEventId(Long eventId);
    List<Request> findAllByIdIn(List<Long> ids);
    List<Request> findAllByRequesterId(Long requesterId);
    Optional<Request> findByIdAndRequesterId(Long id, Long requesterId);
    boolean existsByRequesterIdAndEventId(Long requesterId, Long eventId);
    int countByEventIdAndStatus(Long eventId, RequestStatus status);
}
