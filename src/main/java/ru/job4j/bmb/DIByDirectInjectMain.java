package ru.job4j.bmb;

import ru.job4j.bmb.content.Content;
import ru.job4j.bmb.services.BotCommandHandler;
import ru.job4j.bmb.services.TelegramBotService;

public class DIByDirectInjectMain {
    public static void main(String[] args) {
        var handler = new BotCommandHandler(null, null, null);
        var tg = new TelegramBotService("test", "123", handler);
        Long chatId = 1L;
        //tg.receive(new Content(chatId));
    }
}