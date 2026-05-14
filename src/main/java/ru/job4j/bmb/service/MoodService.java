package ru.job4j.bmb.service;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.job4j.bmb.content.Content;
import ru.job4j.bmb.model.*;
import ru.job4j.bmb.repository.AchievementRepository;
import ru.job4j.bmb.repository.MoodLogRepository;
import ru.job4j.bmb.repository.MoodRepository;
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
    private final MoodRepository moodRepository;
    private final DateTimeFormatter formatter = DateTimeFormatter
            .ofPattern("dd-MM-yyyy HH:mm")
            .withZone(ZoneId.systemDefault());
    private final ApplicationEventPublisher publisher;

    public MoodService(MoodLogRepository moodLogRepository,
                       RecommendationEngine recommendationEngine,
                       UserRepository userRepository,
                       AchievementRepository achievementRepository,
                       MoodRepository moodRepository,
                       ApplicationEventPublisher publisher) {
        this.moodLogRepository = moodLogRepository;
        this.recommendationEngine = recommendationEngine;
        this.userRepository = userRepository;
        this.achievementRepository = achievementRepository;
        this.moodRepository = moodRepository;
        this.publisher = publisher;
    }

    public Content chooseMood(User user, Long moodId) {
        MoodLog log = new MoodLog();
        log.setUser(user);
        log.setCreatedAt(Instant.now().getEpochSecond());
        Mood mood = moodRepository.findById(moodId)
                .orElseThrow(() -> new IllegalArgumentException("Mood not found: " + moodId));
        log.setMood(mood);
        moodLogRepository.save(log);
        publisher.publishEvent(new UserEvent(this, user));
        return recommendationEngine.recommendFor(user.getChatId(), moodId);
    }

    @Transactional
    public Optional<Content> weekMoodLogCommand(Long chatId, Long clientId) {
        long weekAgo = Instant.now()
                .minusSeconds(7 * 24 * 60 * 60)
                .getEpochSecond();
        List<MoodLog> logs = moodLogRepository.findByClientIdAndCreatedAtAfter(clientId, weekAgo);
        var content = new Content(chatId);
        content.setText(formatMoodLogs(logs, "Mood for last 7 days"));
        return Optional.of(content);
    }

    @Transactional
    public Optional<Content> monthMoodLogCommand(Long chatId, Long clientId) {
        Long monthAgo = Instant.now()
                .minusSeconds(30 * 24 * 60 * 60)
                .getEpochSecond();
        List<MoodLog> logs = moodLogRepository.findAll().stream()
                .filter(log -> log.getUser() != null)
                .filter(log -> log.getUser().getClientId().equals(clientId))
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
        var sb = new StringBuilder(title).append(":\n");
        int limit = Math.min(logs.size(), 15);
        for (int i = logs.size() - 1; i >= 0 && limit > 0; i--, limit--) {
            MoodLog log = logs.get(i);
            if (log.getCreatedAt() == null) {
                continue;
            }
            String date = formatter.format(Instant.ofEpochSecond(log.getCreatedAt()));
            String moodText = (log.getMood() != null && log.getMood().getText() != null)
                    ? log.getMood().getText()
                    : "Неизвестно";
            sb.append(date).append(" | ").append(moodText).append("\n");
        }
        if (logs.size() > 15) {
            sb.append("... и ещё ").append(logs.size() - 15).append(" записей");
        }
        String result = sb.toString();
        return result.length() > 4000 ? result.substring(0, 3997) + "..." : result;
    }

    @Transactional
    public Optional<Content> awards(Long chatId, Long clientId) {
        List<Achievement> achievements = achievementRepository.findByClientId(clientId);
        var content = new Content(chatId);
        if (achievements.isEmpty()) {
            content.setText("🏆 У вас пока нет полученных наград. Отмечайте хорошее настроение!");
            return Optional.of(content);
        }
        StringBuilder sb = new StringBuilder("🏆 Ваши полученные награды:\n");
        achievements.forEach(a -> {
            if (a.getAward() != null) {
                sb.append("--").append(a.getAward().getTitle())
                        .append(": ").append(a.getAward().getDescription()).append("\n");
            }
        });
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
