package com.example.aifullstack.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Column;
import java.time.Instant;

@Entity
//表示这个 Java 类对应数据库中的一张表。
public class ChatMessage {
    @Id//表示 id 是每条记录的唯一编号。
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 1000, nullable = false)
    private String question;

    @Column(length = 10000, nullable = false)
    private String answer;
//    分别保存用户问题和系统回答。
    @Column(length = 2000)
    private String sources;

    private Instant createdAt;
//    保存对话产生的时间。

    protected ChatMessage() {}

    public ChatMessage(String question, String answer, String sources, Instant createdAt) {
        this.question = question;
        this.answer = answer;
        this.sources = sources;
        this.createdAt = createdAt;
    }

    public Long getId() { return id; }
    public String getQuestion() { return question; }
    public String getAnswer() { return answer; }
    public String getSources() { return sources; }
    public Instant getCreatedAt() { return createdAt; }
}
