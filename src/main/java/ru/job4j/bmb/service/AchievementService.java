package ru.job4j.bmb.service;

import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.job4j.bmb.content.Content;
import ru.job4j.bmb.model.Achievement;
import ru.job4j.bmb.model.UserEvent;
import ru.job4j.bmb.repository.AchievementRepository;
import ru.job4j.bmb.repository.AwardRepository;
import ru.job4j.bmb.repository.MoodLogRepository;
import ru.job4j.bmb.services.SentContent;

import java.time.Instant;

@Service
public class AchievementService implements ApplicationListener<UserEvent> {

    private final MoodLogRepository moodLogRepository;
    private final AchievementRepository achievementRepository;
    private final AwardRepository awardRepository;
    private final SentContent sentContent;

    public AchievementService(MoodLogRepository moodLogRepository,
                              AchievementRepository achievementRepository,
                              AwardRepository awardRepository,
                              SentContent sentContent) {
        this.moodLogRepository = moodLogRepository;
        this.achievementRepository = achievementRepository;
        this.awardRepository = awardRepository;
        this.sentContent = sentContent;
    }

    @Transactional
    @Override
    public void onApplicationEvent(UserEvent event) {
        var user = event.getUser();

        long count = moodLogRepository.findByUserId(user.getId()).size();

        awardRepository.findAll().stream()
                .filter(award -> award.getDays() == count)
                .findFirst()
                .ifPresent(award -> {
                    Achievement achievement = new Achievement();
                    achievement.setUser(user);
                    achievement.setAward(award);
                    achievement.setCreateAt(Instant.now().getEpochSecond());
                    achievementRepository.save(achievement);
                    Content content = new Content(user.getChatId());
                    content.setText(
                            "Congratulations! "
                                    + "You received achievement: "
                                    + award.getTitle()
                    );
                    sentContent.sent(content);
                });
    }
}