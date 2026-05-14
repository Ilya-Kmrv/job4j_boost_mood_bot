package ru.job4j.bmb.repository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.job4j.bmb.model.MoodLog;
import ru.job4j.bmb.model.User;

import java.util.List;
import java.util.Optional;

@Repository
public interface MoodLogRepository extends CrudRepository<MoodLog, Long> {
    List<MoodLog> findAll();

    List<MoodLog> findByUserId(Long userId);

    @Query("""
            select distinct u from User u
            where not exists (
                select 1 from MoodLog m
                where m.user.id = u.id
                and m.createdAt between :start and :end
            )
            """)
    List<User> findUsersWhoDidNotVoteToday(
            @Param("start") Long startOfDay,
            @Param("end") Long endOfDay
    );

    @Query("SELECT ml FROM MoodLog ml JOIN ml.user u WHERE u.clientId = :clientId AND ml.createdAt >= :since ORDER BY ml.createdAt DESC")
    List<MoodLog> findByClientIdAndCreatedAtAfter(@Param("clientId") Long clientId, @Param("since") long since);

    Optional<MoodLog> findTopByUserIdOrderByCreatedAtDesc(Long userId);
}