package ru.job4j.bmb.model;

import jakarta.persistence.*;

import java.util.Objects;

@Entity
@Table(name = "mb_user")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "client_id", unique = true)
    private Long clientId;

    @Column(name = "chat_id")
    private Long chatId;

    @Column(name = "receive_daily_advice")
    private Boolean receiveDailyAdvice = true;

    public User() {
    }

    public User(Long id, Long clientId, Long chatId) {
        this.id = id;
        this.clientId = clientId;
        this.chatId = chatId;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getClientId() {
        return clientId;
    }

    public void setClientId(Long clientId) {
        this.clientId = clientId;
    }

    public long getChatId() {
        return chatId;
    }

    public void setChatId(Long chatId) {
        this.chatId = chatId;
    }

    public Boolean getReceiveDailyAdvice() {
        return receiveDailyAdvice;
    }

    public void setReceiveDailyAdvice(Boolean receiveDailyAdvice) {
        this.receiveDailyAdvice = receiveDailyAdvice;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        User user = (User) o;
        return Objects.equals(id, user.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, clientId, chatId);
    }

    @Override
    public String toString() {
        return "User{"
                + "id="
                + id
                + ", clientId="
                + clientId
                + ", chatId="
                + chatId
                + '}';
    }
}