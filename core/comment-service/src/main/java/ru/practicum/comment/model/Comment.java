package ru.practicum.comment.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "comments")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Comment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private LocalDateTime created;

    @Column(name = "event_id")
    private Long eventId;

    @Column(name = "user_id")
    private Long userId;

    private String text;
}
