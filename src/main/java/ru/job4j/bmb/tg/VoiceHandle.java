package ru.job4j.bmb.tg;

import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;
import java.util.stream.IntStream;

public class VoiceHandle {
    public CompletableFuture<Void> process(String message, Consumer<String> consumer) {
        return CompletableFuture.runAsync(() -> {
            IntStream.range(0, 5).forEach(it -> {
                try {
                    Thread.sleep(1000);
                    consumer.accept(
                            String.format("Message: %s", it)
                    );
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });
        });
    }
}