package ru.practicum;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "event_statistics")
public class EventStatistics {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "views")
    private Long views;

    @Column(name = "unique_visitors")
    private Integer uniqueVisitors;

    @Column(name = "statistic_date", nullable = false)
    private LocalDateTime statisticDate;

    @Column(name = "created", updatable = false)
    private LocalDateTime created;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "event_id", nullable = false)
    private Event event;

    @PrePersist
    protected void onCreate() {
        created = LocalDateTime.now();
        if (views == null) views = 0L;
        if (uniqueVisitors == null) uniqueVisitors = 0;
    }
}
