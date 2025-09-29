package ru.practicum.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.practicum.emuns.EventState;
import ru.practicum.model.Event;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface EventRepository extends JpaRepository<Event, Long> {
    List<Event> findAllByInitiatorId(Long initiatorId, Pageable pageable);

    Optional<Event> findByIdAndInitiatorId(Long id, Long initiatorId);

    boolean existsByIdAndInitiatorId(Long id, Long initiatorId);

    Optional<Event> findByIdAndState(Long id, EventState state);

    @Query("""
            SELECT e FROM Event e
            WHERE e.state = 'PUBLISHED'
                        AND (LOWER(e.annotation) LIKE LOWER(CONCAT('%', COALESCE(:text, ''), '%'))
                            OR LOWER(e.description) LIKE LOWER(CONCAT('%', COALESCE(:text, ''), '%')))
                        AND (:categories IS NULL OR e.category.id IN :categories)
                        AND (COALESCE(:start, e.eventDate) <= e.eventDate)
                        AND (COALESCE(:end, e.eventDate) >= e.eventDate)
                        AND (:paid IS NULL OR e.paid = :paid)""")
    Page<Event> searchEvent(@Param("text") String text,
                            @Param("categories") List<Long> categories,
                            @Param("start") LocalDateTime start,
                            @Param("end") LocalDateTime end,
                            @Param("paid") Boolean paid,
                            Pageable pageable);


    @Query("""
            SELECT e FROM Event e WHERE
            e.state = 'PUBLISHED'
                        AND (LOWER(e.annotation) LIKE LOWER(CONCAT('%', COALESCE(:text, ''), '%'))
                            OR LOWER(e.description) LIKE LOWER(CONCAT('%', COALESCE(:text, ''), '%')))
                        AND (:categories IS NULL OR e.category.id IN :categories)
                        AND e.eventDate >= CURRENT_TIMESTAMP
                        AND (:paid IS NULL OR e.paid = :paid)""")
    Page<Event> searchEventCurrentTime(@Param("text") String text,
                                       @Param("categories") List<Long> categories,
                                       @Param("paid") Boolean paid,
                                       Pageable pageable);

    @Query("""
            SELECT e FROM Event e WHERE
                        (:users IS NULL OR e.initiator.id IN :users) AND
                        (:states IS NULL OR e.state IN :states) AND
                        (:categories IS NULL OR e.category.id IN :categories) AND
                        (e.eventDate >= :start) AND
                        (e.eventDate <= :end)
            """)
    List<Event> searchEvents(@Param("users") List<Long> users,
                             @Param("states") List<EventState> states,
                             @Param("categories") List<Long> categories,
                             @Param("start") LocalDateTime start,
                             @Param("end") LocalDateTime end,
                             Pageable pageable);

    long countByCategoryId(Long categoryId);
}