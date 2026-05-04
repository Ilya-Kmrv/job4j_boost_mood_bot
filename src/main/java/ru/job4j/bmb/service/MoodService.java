package ru.job4j.bmb.service;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import org.springframework.stereotype.Service;
import ru.job4j.bmb.content.Content;
import ru.job4j.bmb.model.Achievement;
import ru.job4j.bmb.model.Mood;
import ru.job4j.bmb.model.MoodLog;
import ru.job4j.bmb.model.User;
import ru.job4j.bmb.repository.AchievementRepository;
import ru.job4j.bmb.repository.MoodLogRepository;
import ru.job4j.bmb.repository.UserRepository;
import ru.job4j.bmb.services.RecommendationEngine;

import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

@Service
public class MoodService {
    private final MoodLogRepository moodLogRepository;
    private final RecommendationEngine recommendationEngine;
    private final UserRepository userRepository;
    private final AchievementRepository achievementRepository;
    private final DateTimeFormatter formatter = DateTimeFormatter
            .ofPattern("dd-MM-yyyy HH:mm")
            .withZone(ZoneId.systemDefault());

    public MoodService(MoodLogRepository moodLogRepository,
                       RecommendationEngine recommendationEngine,
                       UserRepository userRepository,
                       AchievementRepository achievementRepository) {
        this.moodLogRepository = moodLogRepository;
        this.recommendationEngine = recommendationEngine;
        this.userRepository = userRepository;
        this.achievementRepository = achievementRepository;
    }

    public Content chooseMood(User user, Long moodId) {
        MoodLog log = new MoodLog();
        log.setUser(user);
        log.setCreatedAt(Instant.now().getEpochSecond());

        Mood mood = new Mood();
        mood.setId(moodId);
        log.setMood(mood);

        moodLogRepository.save(log);
        return recommendationEngine.recommendFor(user.getChatId(), moodId);
    }

    public Optional<Content> weekMoodLogCommand(Long chatId, Long clientId) {

        long weekAgo = Instant.now()
                .minusSeconds(7 * 24 * 60 * 60)
                .getEpochSecond();

        List<MoodLog> logs = moodLogRepository.findAll().stream()
                .filter(log -> log.getUser() != null)
                .filter(log -> log.getUser().getId().equals(clientId))
                .filter(log -> log.getCreatedAt() >= weekAgo)
                .toList();

        var content = new Content(chatId);
        content.setText(formatMoodLogs(logs, "Mood for last 7 days"));

        return Optional.of(content);
    }

    public Optional<Content> monthMoodLogCommand(Long chatId, Long clientId) {

        long monthAgo = Instant.now()
                .minusSeconds(30 * 24 * 60 * 60)
                .getEpochSecond();

        List<MoodLog> logs = moodLogRepository.findAll().stream()
                .filter(log -> log.getUser() != null)
                .filter(log -> log.getUser().getId().equals(clientId))
                .filter(log -> log.getCreatedAt() >= monthAgo)
                .toList();

        var content = new Content(chatId);
        content.setText(formatMoodLogs(logs, "Mood for last 30 days"));

        return Optional.of(content);
    }

    private String formatMoodLogs(List<MoodLog> logs, String title) {
        if (logs.isEmpty()) {
            return title + ":\n No mood logs found.";
        }
        var sb = new StringBuilder(title + ":\n");
        logs.forEach(log -> {
            String formattedDate = formatter.format(Instant.ofEpochSecond(log.getCreatedAt()));
            sb.append(formattedDate).append(": ").append(log.getMood().getText()).append("\n");
        });
        return sb.toString();
    }

    public Optional<Content> awards(Long chatId, Long clientId) {
        List<Achievement> achievements = achievementRepository.findAll().stream()
                .filter(a -> a.getUser() != null)
                .filter(a -> a.getUser().getId().equals(clientId))
                .toList();
        var content = new Content(chatId);
        if (achievements.isEmpty()) {
            content.setText("No achievements yet.");
            return Optional.of(content);
        }
        StringBuilder stringBuilder = new StringBuilder("Your achievements:\n");
        for (var a : achievements) {
            String awardText = (a.getAward() != null)
                    ? a.getAward().toString() : "Unknown award";
            stringBuilder.append("- ")
                    .append(awardText)
                    .append("\n");
        }
        content.setText(sb.toString());
        return Optional.of(content);
    }

    @PostConstruct
    public void init() {
        System.out.println("Bean is going through @PostConstruct init.");
    }

    @PreDestroy
    public void destroy() {
        System.out.println("Bean will be destroyed via @PreDestroy.");
    }
}
