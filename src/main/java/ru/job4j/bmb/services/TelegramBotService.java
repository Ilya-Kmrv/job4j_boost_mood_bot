package ru.job4j.bmb.services;

import org.jvnet.hk2.annotations.Service;
import ru.job4j.bmb.content.Content;

public class TelegramBotService {
    private final BotCommandHandler handler;

    @Service
    public TelegramBotService(BotCommandHandler handler) {
        this.handler = handler;
    }

    public void receive(Content content) {
        handler.receive(content);
    }
}