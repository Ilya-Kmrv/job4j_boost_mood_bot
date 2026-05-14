package ru.job4j.bmb.model;

import jakarta.persistence.*;

@Entity
@Table(name = "mb_daily_advice")
public class DailyAdvice {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String text;

    private boolean positive;

    public DailyAdvice() {
    }

    public DailyAdvice(String text, boolean positive) {
        this.text = text;
        this.positive = positive;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public boolean isPositive() {
        return positive;
    }

    public void setPositive(boolean positive) {
        this.positive = positive;
    }
}