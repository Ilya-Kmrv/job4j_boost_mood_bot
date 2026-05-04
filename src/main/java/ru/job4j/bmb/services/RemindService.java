package ru.job4j.bmb.services;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import ru.job4j.bmb.content.Content;
import ru.job4j.bmb.repository.UserRepository;

@Service
public class RemindService {
    private final TelegramBotService telegramBotService;
    private final UserRepository userRepository;

    public RemindService(TelegramBotService tgRemoteService, UserRepository userRepository) {
        this.telegramBotService  = tgRemoteService;
        this.userRepository = userRepository;
    }

    //@Scheduled(fixedRateString = "${remind.period}")
    public void ping() {
        for (var user : userRepository.findAll()) {
            var message = new Content(user.getChatId());
            message.setText("Ping");
            telegramBotService.sent(message);
        }
    }
}