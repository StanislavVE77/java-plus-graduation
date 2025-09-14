package ru.practicum.request.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "requests",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = {"requester_id", "event_id"})
        }
)
public class Request {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column
    private LocalDateTime created;

    @Column(name = "requester_id")
    private Long requesterId;

    @Column(name = "event_id")
    private Long eventId;

    @Enumerated(EnumType.STRING)
    private RequestStatus status;
}
