package ru.job4j.bmb.content;

import org.springframework.stereotype.Component;
import ru.job4j.bmb.repository.MoodContentRepository;

@Component
public class ContentProviderText implements ContentProvider {

    private final MoodContentRepository moodContentRepository;

    public ContentProviderText(MoodContentRepository moodContentRepository) {
        this.moodContentRepository = moodContentRepository;
    }

    @Override
    public Content byMood(Long chatId, Long moodId) {
        return moodContentRepository.findById(moodId)
                .map(mc -> {
                    var content = new Content(chatId);
                    content.setText(mc.getText());
                    return content;
                })
                .orElseGet(() -> {
                    var content = new Content(chatId);
                    content.setText("Настроение не найдено");
                    return content;
                });
    }
}
