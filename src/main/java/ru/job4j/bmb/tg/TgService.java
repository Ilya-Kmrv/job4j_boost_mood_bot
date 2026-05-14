package ru.job4j.bmb.tg;

public class TgService extends LongPoll {
    private final VoiceHandle voiceHandle;

    public TgService(VoiceHandle voiceHandle) {
        this.voiceHandle = voiceHandle;
    }

    @Override
    void receive(String message) {
        voiceHandle.process(message, this::sent).join();
    }

    public static void main(String[] args) {
        new TgService(new VoiceHandle()).receive("Hello");
    }
}