package ru.practicum.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name", nullable = false, length = 250)
    private String name;

    @Column(name = "email", nullable = false, unique = true, length = 254)
    private String email;

    @Column(name = "created", updatable = false)
    private LocalDateTime created;

    @Column(name = "status", length = 20)
    private String status;

    @OneToMany(mappedBy = "initiator", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @Builder.Default
    private Set<Event> events = new HashSet<>();

    @OneToMany(mappedBy = "requester", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @Builder.Default
    private Set<Request> requests = new HashSet<>();

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "user_roles",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "role_id")
    )
    @Builder.Default
    private Set<Role> roles = new HashSet<>();

    @OneToMany(mappedBy = "author", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @Builder.Default
    private Set<Comment> comments = new HashSet<>();

    @PrePersist
    protected void onCreate() {
        created = LocalDateTime.now();
        if (status == null) {
            status = "ACTIVE";
        }
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return Objects.equals(id, user.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", email='" + email + '\'' +
                ", created=" + created +
                ", status='" + status + '\'' +
                ", eventsCount=" + (events != null ? events.size() : 0) +
                ", requestsCount=" + (requests != null ? requests.size() : 0) +
                ", rolesCount=" + (roles != null ? roles.size() : 0) +
                ", commentsCount=" + (comments != null ? comments.size() : 0) +
                '}';
    }
}