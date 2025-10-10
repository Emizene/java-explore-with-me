package ru.practicum.model;

import jakarta.persistence.*;
import lombok.*;
import ru.practicum.emuns.EventState;

import java.time.LocalDateTime;
import java.util.*;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "events")
public class Event {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "annotation", nullable = false, length = 2000)
    private String annotation;

    @Column(name = "description", length = 7000)
    private String description;

    @Column(name = "title", nullable = false, length = 120)
    private String title;

    @Column(name = "event_date", nullable = false)
    private LocalDateTime eventDate;

    @Column(name = "created_on", updatable = false)
    private LocalDateTime createdOn;

    @Column(name = "published_on")
    private LocalDateTime publishedOn;

    @Column(name = "paid")
    private Boolean paid;

    @Column(name = "participant_limit")
    private Integer participantLimit;

    @Column(name = "request_moderation")
    private Boolean requestModeration;

    @Enumerated(EnumType.STRING)
    @Column(name = "state", length = 20)
    private EventState state;

    @Column(name = "views", columnDefinition = "text[]")
    private List<String> views;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "initiator_id", nullable = false)
    private User initiator;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id", nullable = false)
    private Category category;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "location_id", nullable = false)
    private Location location;

    @OneToMany(mappedBy = "event", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @Builder.Default
    private Set<Request> requests = new HashSet<>();

    @ManyToMany(mappedBy = "events", fetch = FetchType.LAZY)
    @Builder.Default
    private Set<Compilation> compilations = new HashSet<>();

    @OneToMany(mappedBy = "event", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @Builder.Default
    private Set<Comment> comments = new HashSet<>();

    @PrePersist
    protected void onCreate() {
        createdOn = LocalDateTime.now();
        if (paid == null) paid = false;
        if (participantLimit == null) participantLimit = 0;
        if (requestModeration == null) requestModeration = true;
        if (state == null) state = EventState.PENDING;
        if (views == null) views = new ArrayList<>();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Event event)) return false;
        return id != null && id.equals(event.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }

    @Override
    public String toString() {
        return "Event{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", annotation='" + (annotation != null ?
                (annotation.length() > 30 ? annotation.substring(0, 30) + "..." : annotation)
                : null) + '\'' +
                ", eventDate=" + eventDate +
                ", state=" + state +
                ", paid=" + paid +
                ", participantLimit=" + participantLimit +
                ", initiatorId=" + (initiator != null ? initiator.getId() : null) +
                ", categoryId=" + (category != null ? category.getId() : null) +
                ", locationId=" + (location != null ? location.getId() : null) +
                ", requestsCount=" + (requests != null ? requests.size() : 0) +
                ", commentsCount=" + (comments != null ? comments.size() : 0) +
                ", compilationsCount=" + (compilations != null ? compilations.size() : 0) +
                '}';
    }
}