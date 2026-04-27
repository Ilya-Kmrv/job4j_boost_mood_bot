package ru.job4j.bmb.services;

import org.jvnet.hk2.annotations.Service;
import ru.job4j.bmb.content.Content;

@Service
public class BotCommandHandler {
    void receive(Content content) {
        System.out.println(content);
    }
}