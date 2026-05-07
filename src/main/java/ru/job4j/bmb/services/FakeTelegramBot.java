package ru.job4j.bmb.services;


import org.springframework.context.annotation.Conditional;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.objects.Update;
import ru.job4j.bmb.config.FakeCondition;
import ru.job4j.bmb.content.Content;

@Service
@Conditional(FakeCondition.class)
public class FakeTelegramBot extends TelegramLongPollingBot implements SentContent {

    public FakeTelegramBot() {
        super("FAKE TOKEN");
    }

    @Override
    public void onUpdateReceived(Update update) {
        System.out.println("FAKE UPDATE RECEIVED: " + update);
    }

    @Override
    public String getBotUsername() {
        return "FAKE BOT USERNAME";
    }

    @Override
    public void sent(Content content) {
        System.out.println("=== FAKE BOT ===");
        System.out.println("chatId: " + content.getChatId());
        System.out.println("text: " + content.getText());
    }
}
