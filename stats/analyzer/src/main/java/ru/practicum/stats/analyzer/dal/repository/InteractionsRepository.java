package ru.practicum.stats.analyzer.dal.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.stats.analyzer.dal.model.UserAction;

import java.util.List;
import java.util.Set;

public interface InteractionsRepository extends JpaRepository<UserAction, Long> {

    UserAction findByUserIdAndEventId(Long userId, Long eventId);

    List<UserAction> findByEventIdIn(Set<Long> eventIds);

    List<UserAction> findByUserId(Long userId);

    @Query("select distinct e from UserAction e where e.eventId not in ( select u.eventId from UserAction u where u.userId = ?1)")
    List<UserAction> findNotByUserId(Long userId);

}
