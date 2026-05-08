package ru.job4j.bmb.repository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.job4j.bmb.model.MoodLog;
import ru.job4j.bmb.model.User;

import java.util.List;
import java.util.stream.Stream;

@Repository
public interface MoodLogRepository extends CrudRepository<MoodLog, Long> {
    List<MoodLog> findAll();

    List<MoodLog> findByUserId(Long userId);

    //Stream<MoodLog> findByUserIdOrderByCreatedAtDesc(Long userId);

    //List<User> findUsersWhoDidNotVoteToday(Long startOfDay, Long endOfDay);

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

    //List<MoodLog> findMoodLogsForWeek(Long userId, Long weekStart);

    //List<MoodLog> findMoodLogsForMonth(Long userId, Long monthStart);
}