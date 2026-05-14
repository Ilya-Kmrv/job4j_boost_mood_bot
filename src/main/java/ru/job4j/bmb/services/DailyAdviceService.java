package ru.job4j.bmb.services;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.job4j.bmb.content.Content;
import ru.job4j.bmb.model.DailyAdvice;
import ru.job4j.bmb.model.User;
import ru.job4j.bmb.repository.DailyAdviceRepository;
import ru.job4j.bmb.repository.MoodLogRepository;
import ru.job4j.bmb.repository.UserRepository;

import java.util.List;
import java.util.Optional;
import java.util.Random;

@Service
public class DailyAdviceService {

    private final DailyAdviceRepository adviceRepo;
    private final MoodLogRepository moodLogRepo;
    private final UserRepository userRepo;
    private final SentContent sentContent;
    private static final Random RND = new Random();

    public DailyAdviceService(DailyAdviceRepository adviceRepo,
                              MoodLogRepository moodLogRepo,
                              UserRepository userRepo,
                              SentContent sentContent) {
        this.adviceRepo = adviceRepo;
        this.moodLogRepo = moodLogRepo;
        this.userRepo = userRepo;
        this.sentContent = sentContent;
    }

    public Content getAdviceForUser(User user) {
        boolean isGood = moodLogRepo.findTopByUserIdOrderByCreatedAtDesc(user.getId())
                .map(log -> log.getMood() != null && log.getMood().isGood())
                .orElse(true);

        List<DailyAdvice> candidates = adviceRepo.findByPositive(isGood);
        DailyAdvice advice = candidates.isEmpty()
                ? new DailyAdvice("Просто сделайте глубокий вдох. Вы справитесь.", false)
                : candidates.get(RND.nextInt(candidates.size()));

        var content = new Content(user.getChatId());
        content.setText("Совет дня:\n" + advice.getText());
        return content;
    }

    @Transactional(readOnly = true)
    public Optional<Content> handleManualAdvice(Long chatId, Long clientId) {
        var userOpt = userRepo.findByClientId(clientId);
        return userOpt.map(this::getAdviceForUser);
    }

    @Transactional
    public Content toggleAutoAdvice(Long chatId, Long clientId) {
        var user = userRepo.findByClientId(clientId).orElseThrow();
        boolean current = Boolean.TRUE.equals(user.getReceiveDailyAdvice());
        user.setReceiveDailyAdvice(!current);
        userRepo.save(user);

        var content = new Content(chatId);
        String status = user.getReceiveDailyAdvice() ? "ВКЛЮЧЕНЫ" : "ОТКЛЮЧЕНЫ";
        content.setText("Автоматические советы " + status);
        return content;
    }

    @Scheduled(cron = "${advice.cron.expression}")
    public void sendDailyAdviceToAll() {
        userRepo.findAll().forEach(user -> {
            if (Boolean.TRUE.equals(user.getReceiveDailyAdvice())) {
                try {
                    sentContent.sent(getAdviceForUser(user));
                } catch (Exception e) {
                    System.err.println("Ошибка отправки совета пользователю " + user.getClientId() + ": " + e.getMessage());
                }
            }
        });
    }
}