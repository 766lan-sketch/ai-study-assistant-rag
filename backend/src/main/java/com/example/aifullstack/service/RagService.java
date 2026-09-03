package com.example.aifullstack.service;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

@Service
public class RagService {
    private final List<KnowledgeChunk> knowledgeBase;

    public RagService() {
        this.knowledgeBase = loadKnowledgeBase();
    }

    public List<KnowledgeChunk> retrieve(String question) {
        String normalized = question.toLowerCase(Locale.ROOT);
        return knowledgeBase.stream()
                .map(chunk -> new ScoredChunk(chunk, score(chunk, normalized)))
                .filter(item -> item.score() > 0)
                .sorted(Comparator.comparingInt(ScoredChunk::score).reversed())
                .limit(2)
                .map(ScoredChunk::chunk)
                .toList();
    }

    public String fallbackAnswer(String question, List<KnowledgeChunk> chunks) {
        if (chunks.isEmpty()) {
            return "知识库中暂时没有找到与“" + question + "”相关的内容，请换一个 Java 基础问题试试。";
        }
        return chunks.getFirst().content();
    }

    private int score(KnowledgeChunk chunk, String question) {
        int score = 0;
        for (String keyword : chunk.keywords()) {
            if (question.contains(keyword.toLowerCase(Locale.ROOT))) {
                score += keyword.length() + 2;
            }
        }
        for (String token : question.split("[^a-z0-9]+")) {
            if (token.length() > 2 && (chunk.title() + chunk.content()).toLowerCase(Locale.ROOT).contains(token)) {
                score++;
            }
        }
        return score;
    }

    private List<KnowledgeChunk> loadKnowledgeBase() {
        try {
            String text = new ClassPathResource("knowledge-base.md")
                    .getContentAsString(StandardCharsets.UTF_8);
            return Arrays.stream(text.split("\\R---\\R"))
                    .map(String::trim)
                    .filter(block -> !block.isBlank())
                    .map(this::parseChunk)
                    .toList();
        } catch (IOException exception) {
            throw new IllegalStateException("无法读取本地知识库", exception);
        }
    }

    private KnowledgeChunk parseChunk(String block) {
        String[] lines = block.split("\\R", 3);
        String title = lines[0].replaceFirst("^#\\s*", "").trim();
        List<String> keywords = Arrays.stream(lines[1].replaceFirst("^关键词：", "").split(","))
                .map(String::trim).filter(item -> !item.isBlank()).toList();
        return new KnowledgeChunk(title, keywords, lines.length > 2 ? lines[2].trim() : "");
    }

    private record ScoredChunk(KnowledgeChunk chunk, int score) {}
}
