package ru.practicum.stats.analyzer.dal.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.stats.analyzer.dal.model.EventSimilarity;

import java.util.List;

public interface SimilaritiesRepository extends JpaRepository<EventSimilarity, Long> {

    EventSimilarity findByEventAAndEventB(Long eventA, Long eventB);

    @Query("select u from EventSimilarity u where u.eventA = ?1 or u.eventB = ?1")
    List<EventSimilarity> findByEventAOrEventB(Long eventId);


}
