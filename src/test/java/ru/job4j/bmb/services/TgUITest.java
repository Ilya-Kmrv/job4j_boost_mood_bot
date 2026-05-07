package ru.job4j.bmb.services;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import ru.job4j.bmb.model.Mood;
import ru.job4j.bmb.repository.MoodFakeRepository;
import ru.job4j.bmb.repository.MoodRepository;
import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ContextConfiguration(classes = {TgUI.class, MoodFakeRepository.class})
class TgUITest {
    @Autowired
    private TgUI tgUI;
    @Autowired
    private MoodRepository moodRepository;

    @Test
    void whenBuildButtonsThenReturnsKeyboardWithCorrectData() {
        Mood mood1 = new Mood("Отлично", true);
        mood1.setId(1L);
        Mood mood2 = new Mood("Нормально", false);
        mood2.setId(2L);
        moodRepository.save(mood1);
        moodRepository.save(mood2);
        InlineKeyboardMarkup markup = tgUI.buildButtons();
        assertThat(markup).isNotNull();
        var keyboard = markup.getKeyboard();
        assertThat(keyboard).hasSize(2);
        var btn1 = keyboard.get(0).get(0);
        assertThat(btn1.getText()).isEqualTo("Отлично");
        assertThat(btn1.getCallbackData()).isEqualTo("1");
        var btn2 = keyboard.get(1).get(0);
        assertThat(btn2.getText()).isEqualTo("Нормально");
        assertThat(btn2.getCallbackData()).isEqualTo("2");
    }
}
