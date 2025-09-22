package ru.practicum.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.practicum.emuns.RequestStatus;
import ru.practicum.model.Event;
import ru.practicum.emuns.EventState;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface EventRepository extends JpaRepository<Event, Long> {
    List<Event> findAllByInitiatorId(Long initiatorId, Pageable pageable);

    Optional<Event> findByIdAndInitiatorId(Long id, Long initiatorId);

    boolean existsByIdAndInitiatorId(Long id, Long initiatorId);

    Optional<Event> findByIdAndState(Long id, EventState state);

    @Query("SELECT e FROM Event e " +
            "WHERE e.state = 'PUBLISHED' " +
            "AND (LOWER(e.annotation) LIKE LOWER(CONCAT('%', COALESCE(:text, ''), '%')) " +
            "    OR LOWER(e.description) LIKE LOWER(CONCAT('%', COALESCE(:text, ''), '%'))) " +
            "AND (:categories IS NULL OR e.category.id IN :categories) " +
            "AND (COALESCE(:start, e.eventDate) <= e.eventDate) " +
            "AND (COALESCE(:end, e.eventDate) >= e.eventDate) " +
            "AND (:paid IS NULL OR e.paid = :paid)")
    Page<Event> searchEvent(@Param("text") String text,
                            @Param("categories") List<Long> categories,
                            @Param("start") LocalDateTime start,
                            @Param("end") LocalDateTime end,
                            @Param("paid") Boolean paid,
                            Pageable pageable);


    @Query("SELECT e FROM Event e " +
            "WHERE e.state = 'PUBLISHED' " +
            "AND (LOWER(e.annotation) LIKE LOWER(CONCAT('%', COALESCE(:text, ''), '%')) " +
            "    OR LOWER(e.description) LIKE LOWER(CONCAT('%', COALESCE(:text, ''), '%'))) " +
            "AND (:categories IS NULL OR e.category.id IN :categories) " +
            "AND e.eventDate >= CURRENT_TIMESTAMP " +
            "AND (:paid IS NULL OR e.paid = :paid)")
    Page<Event> searchEventCurrentTime(@Param("text") String text,
                                       @Param("categories") List<Long> categories,
                                       @Param("paid") Boolean paid,
                                       Pageable pageable);

    @Query("SELECT e FROM Event e " +
            "WHERE (:state IS NULL OR e.state IN :state) " +
            "AND (:users IS NULL OR e.initiator.id IN :users) " +
            "AND (:categories IS NULL OR e.category.id IN :categories) " +
            "AND (COALESCE(:start, e.eventDate) <= e.eventDate) " +
            "AND (COALESCE(:end, e.eventDate) >= e.eventDate)")
    Page<Event> searchEventAdmin(@Param("users") List<Long> users,
                                 @Param("state") List<RequestStatus> state,
                                 @Param("categories") List<Long> categories,
                                 @Param("start") LocalDateTime start,
                                 @Param("end") LocalDateTime end,
                                 Pageable pageable);

    @Query("""
            SELECT e FROM Event e WHERE
                        (:users IS NULL OR e.initiator.id IN :users) AND
                        (:states IS NULL OR e.state IN :states) AND
                        (:categories IS NULL OR e.category.id IN :categories) AND
                        (:start IS NULL OR e.eventDate >= :start) AND
                        (:end IS NULL OR e.eventDate <= :end)
            """)
    List<Event> searchEvents(@Param("users") List<Long> users,
                             @Param("states") List<EventState> states,
                             @Param("categories") List<Long> categories,
                             @Param("start") LocalDateTime start,
                             @Param("end") LocalDateTime end,
                             Pageable pageable);

    @Query("""
            SELECT e FROM Event e
            WHERE (e.state = 'PUBLISHED')
            AND (:text IS NULL OR LOWER(e.annotation) LIKE LOWER(CONCAT('%', :text, '%')) OR LOWER(e.description) LIKE LOWER(CONCAT('%', :text, '%')))
            AND (:categories IS NULL OR e.category.id IN :categories)
            AND (:paid IS NULL OR e.paid = :paid)
            AND (:start IS NULL OR e.eventDate >= :start)
            AND (:end IS NULL OR e.eventDate <= :end)
            AND (:onlyAvailable IS NULL OR
                 CASE WHEN :onlyAvailable = true
                      THEN (e.participantLimit = 0 OR e.participantLimit > e.confirmedRequests)
                      ELSE true
                 END)
            """)
    List<Event> findPublicEvents(@Param("text") String text,
                                 @Param("categories") List<Long> categories,
                                 @Param("paid") Boolean paid,
                                 @Param("start") LocalDateTime start,
                                 @Param("end") LocalDateTime end,
                                 @Param("onlyAvailable") Boolean onlyAvailable,
                                 Pageable pageable);

    long countByCategoryId(Long categoryId);
}
