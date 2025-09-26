package ru.practicum.stats.analyzer.dal.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "similarities")
public class EventSimilarity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "event1", nullable = false)
    private Long eventA;

    @Column(name = "event2", nullable = false)
    private Long eventB;

    @Column(nullable = false)
    private Double similarity;

    @Column(nullable = false)
    private Instant ts;

}
